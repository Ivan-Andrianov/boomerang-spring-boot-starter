package ru.astondevs.kafka.autoconfigure.producer;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.lang.NonNull;
import ru.astondevs.kafka.autoconfigure.KafkaConfigurationProperties;
import ru.astondevs.kafka.autoconfigure.annotation.KafkaProducer;

/**
 * Пост процессор компонента, который связывает продюсера с соответствующим KafkaTemplate.
 *
 * @author Ivan Andrianov
 * @author Maksim Yaskov
 *
 * @see KafkaProducer
 * @see AbstractKafkaProducer
 */
@RequiredArgsConstructor
public class KafkaProducerBeanPostProcessor implements BeanPostProcessor {

    /**
     * Фабрика компонентов.
     */
    private final ConfigurableListableBeanFactory beanFactory;

    /**
     * Свойства конфигураций.
     */
    private final KafkaConfigurationProperties properties;

    @Override
    public Object postProcessBeforeInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
        KafkaProducer annotation = beanFactory.findAnnotationOnBean(beanName, KafkaProducer.class);
        if (annotation == null) {
            return bean;
        }

        if (!(bean instanceof AbstractKafkaProducer<?,?> producer)) {
            throw new BeanNotOfRequiredTypeException(beanName, AbstractKafkaProducer.class, bean.getClass());
        }

        KafkaConfigurationProperties.ProducerProperties properties = this.properties.getProducers().get(annotation.value());
        if (properties == null) {
            throw new IllegalArgumentException("Producer's properties is null");
        }

        ProducerFactoryBuilder producerFactoryBuilder = ProducerFactoryBuilder.of(properties);

        boolean configureKeySerializer = true;
        String keySerializerBeanName = properties.getKeySerializerBeanName();
        if (keySerializerBeanName != null) {
            Serializer<?> keySerializer = (Serializer<?>) beanFactory.getBean(keySerializerBeanName);
            producerFactoryBuilder.keySerializer(keySerializer);
            producerFactoryBuilder.config(config -> config.remove(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG));
            configureKeySerializer = false;
        }

        boolean configureValueSerializer = true;
        String valueSerializerBeanName = properties.getValueSerializerBeanName();
        if (valueSerializerBeanName != null) {
            Serializer<?> valueSerializer = (Serializer<?>) beanFactory.getBean(valueSerializerBeanName);
            producerFactoryBuilder.valueSerializer(valueSerializer);
            producerFactoryBuilder.config(config -> config.remove(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG));
            configureValueSerializer = false;
        }

        producerFactoryBuilder.configureSerializers(configureKeySerializer || configureValueSerializer);

        ProducerFactory<?,?> producerFactory = producerFactoryBuilder.build();
        KafkaTemplate kafkaTemplate = new KafkaTemplate<>(producerFactory);
        kafkaTemplate.setDefaultTopic(properties.getTopic());
        producer.setKafkaTemplate(kafkaTemplate);

        return bean;
    }
}
