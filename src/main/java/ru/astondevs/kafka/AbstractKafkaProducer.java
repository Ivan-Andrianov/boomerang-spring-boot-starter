package ru.astondevs.kafka;

import org.springframework.kafka.core.KafkaTemplate;

public abstract class AbstractKafkaProducer<K,V> {

    protected AbstractKafkaProducer(KafkaTemplate<Long, Event> kafkaTemplate) {

    }

}
