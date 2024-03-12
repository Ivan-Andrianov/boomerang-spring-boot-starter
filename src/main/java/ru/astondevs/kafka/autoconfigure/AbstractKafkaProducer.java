package ru.astondevs.kafka.autoconfigure;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

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
     * Отправляет сообщение в kafka.
     *
     * @param key ключ
     * @param value значение
     * @return результат отправки сообщения
     *
     * @see KafkaTemplate#sendDefault
     */
    public CompletableFuture<SendResult<K,V>> send(K key, V value) {
        return kafkaTemplate.sendDefault(key, value);
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
