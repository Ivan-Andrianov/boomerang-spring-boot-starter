package ru.astondevs.kafka.autoconfigure;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(AutoConfigurationKafkaProperties.class)
public class KafkaAutoConfiguration {

    @Bean
    public KafkaProducerBeanPostProcessor kafkaProducerBeanPostProcessor(ApplicationContext context, AutoConfigurationKafkaProperties properties) {
        return new KafkaProducerBeanPostProcessor(context, properties);
    }

    @Bean
    public KafkaConsumerBeanPostProcessor kafkaListenerBeanPostProcessor(ConfigurableListableBeanFactory factory, AutoConfigurationKafkaProperties properties) {
        return new KafkaConsumerBeanPostProcessor(factory, properties);
    }

}
