package ru.astondevs.kafka.autoconfigure;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(AutoConfigurationKafkaProperties.class)
public class KafkaAutoConfiguration {

}
