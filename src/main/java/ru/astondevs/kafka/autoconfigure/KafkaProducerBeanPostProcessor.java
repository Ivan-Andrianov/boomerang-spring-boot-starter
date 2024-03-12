package ru.astondevs.kafka.autoconfigure;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.lang.NonNull;

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

        ProducerFactory producerFactory = ProducerFactoryFactory.createProducerFactory(properties.getProducers().get(annotation.value()));
        KafkaTemplate kafkaTemplate = new KafkaTemplate<>(producerFactory);
        kafkaTemplate.setDefaultTopic(properties.getProducers().get(annotation.value()).getTopic());
        producer.setKafkaTemplate(kafkaTemplate);

        return bean;
    }
}
