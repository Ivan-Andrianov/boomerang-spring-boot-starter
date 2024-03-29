package ru.astondevs.kafka.autoconfigure;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import ru.astondevs.kafka.autoconfigure.consumer.KafkaConsumer;
import ru.astondevs.kafka.autoconfigure.producer.KafkaProducer;
import ru.astondevs.kafka.autoconfigure.consumer.KafkaConsumerBeanPostProcessor;
import ru.astondevs.kafka.autoconfigure.producer.KafkaProducerBeanPostProcessor;

/**
 * Класс авто-конфигурации kafka.
 *
 * @author Ivan Andrianov
 * @author Maksim Yaskov
 */
@AutoConfiguration
@EnableConfigurationProperties(KafkaConfigurationProperties.class)
@ConditionalOnProperty(prefix = "ru.astondevs.kafka", name = "enabled", havingValue = "true", matchIfMissing = true)
public class KafkaAutoConfiguration {

    /**
     * {@link BeanPostProcessor} отвечающий за конфигурацию {@link KafkaProducer} компонентов.
     */
    @Bean
    public KafkaProducerBeanPostProcessor kafkaProducerBeanPostProcessor(ConfigurableListableBeanFactory beanFactory, KafkaConfigurationProperties properties) {
        return new KafkaProducerBeanPostProcessor(beanFactory, properties);
    }

    /**
     * {@link BeanPostProcessor} отвечающий за конфигурацию {@link KafkaConsumer} компонентов.
     */
    @Bean
    public KafkaConsumerBeanPostProcessor kafkaListenerBeanPostProcessor(ConfigurableListableBeanFactory factory, KafkaConfigurationProperties properties) {
        return new KafkaConsumerBeanPostProcessor(factory, properties);
    }

}
