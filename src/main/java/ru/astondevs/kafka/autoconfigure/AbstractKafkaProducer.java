package ru.astondevs.kafka.autoconfigure;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public abstract class AbstractKafkaProducer<K,V> {

    private KafkaTemplate<K,V> kafkaTemplate;

    public CompletableFuture<SendResult<K,V>> send(K key, V value) {
        return kafkaTemplate.sendDefault(key, value);
    }

    public final KafkaTemplate<K,V> getKafkaTemplate() {
        return kafkaTemplate;
    }

    public final void setKafkaTemplate(KafkaTemplate<K,V> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

}
