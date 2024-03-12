package ru.astondevs.kafka.autoconfigure;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * Класс авто-конфигурации kafka.
 *
 * @author Ivan Andrianov
 * @author Maksim Yaskov
 */
@AutoConfiguration
@EnableConfigurationProperties(AutoConfigurationKafkaProperties.class)
public class KafkaAutoConfiguration {

    /**
     * {@link BeanPostProcessor} отвечающий за конфигурацию {@link KafkaProducer} компонентов.
     */
    @Bean
    public KafkaProducerBeanPostProcessor kafkaProducerBeanPostProcessor(ApplicationContext context, AutoConfigurationKafkaProperties properties) {
        return new KafkaProducerBeanPostProcessor(context, properties);
    }

    /**
     * {@link BeanPostProcessor} отвечающий за конфигурацию {@link KafkaConsumer} компонентов.
     */
    @Bean
    public KafkaConsumerBeanPostProcessor kafkaListenerBeanPostProcessor(ConfigurableListableBeanFactory factory, AutoConfigurationKafkaProperties properties) {
        return new KafkaConsumerBeanPostProcessor(factory, properties);
    }

}
