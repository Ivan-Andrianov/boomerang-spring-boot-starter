package ru.astondevs.kafka.autoconfigure;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.lang.NonNull;

@RequiredArgsConstructor
public class KafkaProducerBeanPostProcessor implements BeanPostProcessor {

    private final ApplicationContext context;

    private final KafkaConfigurationProperties properties;

    @Override
    public Object postProcessBeforeInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
        KafkaProducer annotation = context.findAnnotationOnBean(beanName, KafkaProducer.class);

        if (annotation != null && bean instanceof AbstractKafkaProducer<?,?> producer) {

            ProducerFactory producerFactory = ProducerFactoryFactory.createProducerFactory(properties.getProducers().get(annotation.value()));
            KafkaTemplate kafkaTemplate = new KafkaTemplate<>(producerFactory);
            kafkaTemplate.setDefaultTopic(properties.getProducers().get(annotation.value()).getTopic());
            producer.setKafkaTemplate(kafkaTemplate);
        }

        return bean;
    }
}
