package ru.astondevs.kafka.autoconfigure;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;

import java.util.Map;
import java.util.stream.Collectors;

public class ProducerFactoryFactory {

    public static ProducerFactory createProducerFactory(AutoConfigurationKafkaProperties.ProducerProperties producerProperties) {

        return new DefaultKafkaProducerFactory<>(Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, producerProperties.getBootstrapServers().stream().collect(Collectors.joining(",")),
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, producerProperties.getKeySerializer(),
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, producerProperties.getValueSerializer(),
                ProducerConfig.BATCH_SIZE_CONFIG, producerProperties.getBatchSize(),
                ProducerConfig.LINGER_MS_CONFIG, producerProperties.getLingerMs()
        ));
    }
}
