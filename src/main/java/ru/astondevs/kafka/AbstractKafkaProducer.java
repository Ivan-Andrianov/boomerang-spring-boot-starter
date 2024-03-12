package ru.astondevs.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;

@RequiredArgsConstructor
public abstract class AbstractKafkaProducer<K,V> {

    private final KafkaTemplate<K,V> kafkaTemplate;

    public final KafkaTemplate<K,V> getKafkaTemplate() {
        return kafkaTemplate;
    }

}
