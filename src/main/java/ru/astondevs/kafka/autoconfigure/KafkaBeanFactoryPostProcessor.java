package ru.astondevs.kafka.autoconfigure;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.stereotype.Component;
import ru.astondevs.kafka.AbstractKafkaProducer;


@Component
@RequiredArgsConstructor
public class KafkaBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    public final AutoConfigurationKafkaProperties properties;

    @SneakyThrows
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

        for (String beanDefinitionName : beanFactory.getBeanDefinitionNames()) {

            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanDefinitionName);
            String beanClassName = beanDefinition.getBeanClassName();
            Class<?> beanClass = Class.forName(beanClassName);

            if (!isKafkaProducer(beanClass)) {
                return;
            }

            String beanFactoryName = beanClass.getAnnotation(KafkaProducer.class).value();
            AutoConfigurationKafkaProperties.ProducerProperties producerProperties = properties.getProducers().get(beanFactoryName);
            ProducerFactory producerFactory = ProducerFactoryFactory.createProducerFactory(producerProperties);
            KafkaTemplate kafkaTemplate = new KafkaTemplate(producerFactory);

            beanDefinition.getConstructorArgumentValues().addIndexedArgumentValue(0, kafkaTemplate);
            beanDefinition.getConstructorArgumentValues().addIndexedArgumentValue(1, producerProperties.getTopic());
        }
    }

    private boolean isKafkaProducer(Class<?> beanClass) {
        return beanClass.isAnnotationPresent(KafkaProducer.class) &&
                AbstractKafkaProducer.class.isAssignableFrom(beanClass);
    }
}
