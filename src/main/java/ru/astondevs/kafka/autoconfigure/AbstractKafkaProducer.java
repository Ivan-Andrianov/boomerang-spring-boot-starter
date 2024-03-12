package ru.astondevs.kafka.autoconfigure;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

/**
 * Базовый тип для всех продюсеров в kafka.
 *
 * @see KafkaConsumer
 * @author Максим Яськов
 *
 * @param <K> ключ
 * @param <V> значение
 */
public abstract class AbstractKafkaProducer<K,V> implements InitializingBean {

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

    @Override
    public void afterPropertiesSet() throws Exception {
        if (kafkaTemplate == null) {
            throw new IllegalStateException("kafkaTemplate must not be null");
        }
    }
}
