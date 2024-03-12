package ru.astondevs.kafka.autoconfigure.producer;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import ru.astondevs.kafka.autoconfigure.annotation.KafkaProducer;

import java.util.concurrent.CompletableFuture;

/**
 * Базовый тип для всех продюсеров в kafka.
 *
 * @author Ivan Andrianov
 * @author Maksim Yaskov
 *
 * @param <K> ключ
 * @param <V> значение
 *
 * @see KafkaProducer
 */
public abstract class AbstractKafkaProducer<K,V> implements InitializingBean {

    /**
     * Экземпляр KafkaTemplate специально созданный для продюсера.
     * В этот экземпляр установлен топик по умолчанию.
     */
    private KafkaTemplate<K,V> kafkaTemplate;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (kafkaTemplate == null) {
            throw new IllegalStateException("kafkaTemplate must not be null");
        }
    }

    /**
     * Отправляет указанное значение в Kafka.
     *
     * @param value значение
     * @return {@link CompletableFuture} для {@link SendResult}
     */
    public CompletableFuture<SendResult<K,V>> send(V value) {
        return kafkaTemplate.sendDefault(value);
    }

    /**
     * Отправляет ключ и значение в Kafka.
     *
     * @param key ключ
     * @param value значение
     * @return {@link CompletableFuture} для {@link SendResult}
     */
    public CompletableFuture<SendResult<K,V>> send(K key, V value) {
        return kafkaTemplate.sendDefault(key, value);
    }

    /**
     * Отправляет указанное значение в Kafka с partition и ключом.
     *
     * @param partition partition
     * @param key ключ
     * @param value значение
     * @return {@link CompletableFuture} для {@link SendResult}
     */
    public CompletableFuture<SendResult<K,V>> send(Integer partition, K key, V value) {
        return kafkaTemplate.sendDefault(partition, key, value);
    }

    /**
     * Отправляет указанное значение в Kafka с partition, ключом и timestamp.
     *
     * @param partition partition
     * @param timestamp timestamp
     * @param key ключ
     * @param value значение
     * @return {@link CompletableFuture} для {@link SendResult}
     */
    public CompletableFuture<SendResult<K,V>> send(Integer partition, Long timestamp, K key, V value) {
        return kafkaTemplate.sendDefault(partition, timestamp, key, value);
    }

    /**
     * Возвращает KafkaTemplate этого продюсера.
     *
     * @return KafkaTemplate этого продюсера
     */
    public final KafkaTemplate<K,V> getKafkaTemplate() {
        return kafkaTemplate;
    }

    /**
     * Устанавливает KafkaTemplate для этого продюсера.
     */
    public final void setKafkaTemplate(KafkaTemplate<K,V> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
}
