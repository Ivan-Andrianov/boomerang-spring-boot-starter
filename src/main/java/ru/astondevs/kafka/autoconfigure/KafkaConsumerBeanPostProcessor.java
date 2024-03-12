package ru.astondevs.kafka.autoconfigure;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.kafka.config.AbstractKafkaListenerContainerFactory;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.AbstractMessageListenerContainer;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Пост процессор компонента, который регистрирует компонент для вызова контейнером потребителей сообщений kafka.
 *
 * @author Ivan Andrianov
 * @author Maksim Yaskov
 *
 * @see KafkaConsumer
 * @see AbstractKafkaConsumer
 */
@RequiredArgsConstructor
public class KafkaConsumerBeanPostProcessor implements BeanPostProcessor, DisposableBean {

    /**
     * Карта названия конфигурации и AbstractKafkaListenerContainerFactory.
     */
    private final Map<String, AbstractKafkaListenerContainerFactory<?,?,?>> configurationContainerFactoryMap = new HashMap<>();

    /**
     * Карта beanName и названия конфигурации.
     */
    private final Map<String, String> beanNameConfigurationMap = new HashMap<>();

    /**
     * Карта beanName и AbstractMessageListenerContainer.
     */
    private final Map<String, AbstractMessageListenerContainer<?,?>> beanNameContainerMap = new HashMap<>();

    /**
     * Фабрика компонентов.
     */
    private final ConfigurableListableBeanFactory beanFactory;

    /**
     * Свойства конфигураций.
     */
    private final AutoConfigurationKafkaProperties properties;

    @Override
    public void destroy() {
        beanNameContainerMap.forEach((beanName, container) -> container.stop());
        beanNameContainerMap.clear();
    }

    @Override
    public Object postProcessBeforeInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
        KafkaConsumer kafkaConsumer = beanFactory.findAnnotationOnBean(beanName, KafkaConsumer.class);
        if (kafkaConsumer == null) {
            return bean;
        }

        if (!(bean instanceof AbstractKafkaConsumer<?,?>)) {
            throw new BeanNotOfRequiredTypeException(beanName, AbstractKafkaConsumer.class, bean.getClass());
        }

        beanNameConfigurationMap.put(beanName, kafkaConsumer.value());

        AutoConfigurationKafkaProperties.ConsumerProperties consumerProperties = properties.getConsumers().get(kafkaConsumer.value());
        AbstractKafkaListenerContainerFactory<?, ?, ?> containerFactory = configurationContainerFactoryMap.get(kafkaConsumer.value());
        if (containerFactory == null) {
            containerFactory = createContainerFactory(beanFactory, consumerProperties);
            configurationContainerFactoryMap.put(kafkaConsumer.value(), containerFactory);
        }

        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
        String configuration = beanNameConfigurationMap.get(beanName);
        if (configuration == null) {
            return bean;
        }

        AutoConfigurationKafkaProperties.ConsumerProperties consumerProperties = properties.getConsumers().get(configuration);
        AbstractKafkaListenerContainerFactory<?, ?, ?> containerFactory = configurationContainerFactoryMap.get(configuration);

        AbstractMessageListenerContainer<?,?> container = containerFactory.createContainer(consumerProperties.getTopic());
        container.getContainerProperties().setGroupId(consumerProperties.getGroupId());
        container.getContainerProperties().setMessageListener(bean);
        container.start();

        beanNameContainerMap.put(beanName, container);
        return bean;
    }

    /**
     * Создает {@link AbstractKafkaListenerContainerFactory} используя конфигурацию потребителя.
     *
     * @param beanFactory фабрика компонентов
     * @param consumerProperties конфигурация потребителя
     * @return {@link AbstractKafkaListenerContainerFactory} соответствующую конфигурации
     */
    private AbstractKafkaListenerContainerFactory<?, ?, ?> createContainerFactory(
            ConfigurableListableBeanFactory beanFactory, AutoConfigurationKafkaProperties.ConsumerProperties consumerProperties) {
        AbstractKafkaListenerContainerFactory<?,?,?> factory = new ConcurrentKafkaListenerContainerFactory<>();

        ConsumerFactory consumerFactory = createConsumerFactory(beanFactory, consumerProperties);
        factory.setConsumerFactory(consumerFactory);

        return factory;
    }

    /**
     * Создает {@link ConsumerFactory} используя конфигурацию потребителя.
     *
     * @param beanFactory фабрика компонентов
     * @param consumerProperties конфигурация потребителя
     * @return {@link ConsumerFactory} соответствующую конфигурации
     */
    private <K,V> ConsumerFactory<K, V> createConsumerFactory(ConfigurableListableBeanFactory beanFactory, AutoConfigurationKafkaProperties.ConsumerProperties consumerProperties) {
        Map<String, Object> consumerConfigProperties = new HashMap<>(Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, StringUtils.collectionToCommaDelimitedString(consumerProperties.getBootstrapServers()),
                ConsumerConfig.GROUP_ID_CONFIG, consumerProperties.getGroupId()));

        String keyDeserializerBeanName = consumerProperties.getKeyDeserializerBeanName();
        String valueDeserializerBeanName = consumerProperties.getValueDeserializerBeanName();

        boolean useDeserializerImpl = keyDeserializerBeanName != null || valueDeserializerBeanName != null;
        if (useDeserializerImpl) {
            Deserializer keyDeserializer = keyDeserializerBeanName == null ? null : (Deserializer<?>) beanFactory.getBean(keyDeserializerBeanName);
            Deserializer valueDeserializer = valueDeserializerBeanName == null ? null : (Deserializer<?>) beanFactory.getBean(valueDeserializerBeanName);
            return new DefaultKafkaConsumerFactory<>(consumerConfigProperties, keyDeserializer, valueDeserializer);
        } else {
            consumerConfigProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, consumerProperties.getKeySerializer());
            consumerConfigProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, consumerProperties.getValueSerializer());
            return new DefaultKafkaConsumerFactory<>(consumerConfigProperties);
        }
    }
}
