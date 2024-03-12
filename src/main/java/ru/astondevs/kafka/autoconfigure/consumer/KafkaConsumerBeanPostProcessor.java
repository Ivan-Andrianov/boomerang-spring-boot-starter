package ru.astondevs.kafka.autoconfigure.consumer;

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
import org.springframework.kafka.listener.AbstractMessageListenerContainer;
import org.springframework.lang.NonNull;
import ru.astondevs.kafka.autoconfigure.KafkaConfigurationProperties;
import ru.astondevs.kafka.autoconfigure.annotation.KafkaConsumer;

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
    private final KafkaConfigurationProperties properties;

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

        KafkaConfigurationProperties.ConsumerProperties properties = this.properties.getConsumers().get(kafkaConsumer.value());
        if (properties == null) {
            throw new IllegalStateException("Consumer's properties is null");
        }

        beanNameConfigurationMap.put(beanName, kafkaConsumer.value());

        AbstractKafkaListenerContainerFactory<?, ?, ?> containerFactory = configurationContainerFactoryMap.get(kafkaConsumer.value());
        if (containerFactory == null) {
            containerFactory = createContainerFactory(beanFactory, properties);
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

        KafkaConfigurationProperties.ConsumerProperties properties = this.properties.getConsumers().get(configuration);
        AbstractKafkaListenerContainerFactory<?, ?, ?> containerFactory = configurationContainerFactoryMap.get(configuration);

        AbstractMessageListenerContainer<?,?> container = containerFactory.createContainer(properties.getTopic());
        container.getContainerProperties().setGroupId(properties.getGroupId());
        container.getContainerProperties().setMessageListener(bean);
        container.start();

        beanNameContainerMap.put(beanName, container);
        return bean;
    }

    /**
     * Создает {@link AbstractKafkaListenerContainerFactory} используя конфигурацию потребителя.
     *
     * @param beanFactory фабрика компонентов
     * @param properties конфигурация потребителя
     * @return {@link AbstractKafkaListenerContainerFactory} соответствующую конфигурации
     */
    private AbstractKafkaListenerContainerFactory<?, ?, ?> createContainerFactory(
            ConfigurableListableBeanFactory beanFactory, KafkaConfigurationProperties.ConsumerProperties properties) {
        AbstractKafkaListenerContainerFactory factory = new ConcurrentKafkaListenerContainerFactory<>();
        ConsumerFactoryBuilder consumerFactoryBuilder = ConsumerFactoryBuilder.of(properties);

        boolean configureKeyDeserializer = true;
        String keyDeserializerBeanName = properties.getKeyDeserializerBeanName();
        if (keyDeserializerBeanName != null) {
            Deserializer<?> keyDeserializer = (Deserializer<?>) beanFactory.getBean(keyDeserializerBeanName);
            consumerFactoryBuilder.keyDeserializer(keyDeserializer);
            consumerFactoryBuilder.config(config -> config.remove(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG));
            configureKeyDeserializer = false;
        }

        boolean configureValueDeserializer = true;
        String valueDeserializerBeanName = properties.getValueDeserializerBeanName();
        if (valueDeserializerBeanName != null) {
            Deserializer<?> valueDeserializer = (Deserializer<?>) beanFactory.getBean(valueDeserializerBeanName);
            consumerFactoryBuilder.valueDeserializer(valueDeserializer);
            consumerFactoryBuilder.config(config -> config.remove(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG));
            configureValueDeserializer = false;
        }

        consumerFactoryBuilder.configureDeserializers(configureKeyDeserializer || configureValueDeserializer);

        factory.setConsumerFactory(consumerFactoryBuilder.build());
        return factory;
    }
}
