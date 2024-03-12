package ru.astondevs.kafka.autoconfigure;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ProducerFactoryFactory {

    public static ProducerFactory createProducerFactory(AutoConfigurationKafkaProperties.ProducerProperties producerProperties) {

        Map<String, Object> properties = new HashMap<>();

        properties.put(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                producerProperties.getBootstrapServers().stream().collect(Collectors.joining(","))
        );

        properties.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                producerProperties.getKeySerializer()
        );

        properties.put(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                producerProperties.getValueSerializer()
        );

        properties.put(
                ProducerConfig.BATCH_SIZE_CONFIG,
                producerProperties.getBatchSize()
        );

        properties.put(
                ProducerConfig.LINGER_MS_CONFIG,
                producerProperties.getLingerMs()
        );

        return new DefaultKafkaProducerFactory<>(properties);
    }
}
