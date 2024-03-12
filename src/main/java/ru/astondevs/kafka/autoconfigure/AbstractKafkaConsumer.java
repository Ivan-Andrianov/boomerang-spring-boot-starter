package ru.astondevs.kafka.autoconfigure;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * Базовый тип для всех потребителей из kafka.
 *
 * @see KafkaConsumer
 * @author Максим Яськов
 *
 * @param <K> ключ
 * @param <V> значение
 */
public abstract class AbstractKafkaConsumer<K,V> implements MessageListener<K,V> {

    @Override
    public void onMessage(@NonNull ConsumerRecord<K,V> data, @Nullable Acknowledgment acknowledgment) {
        MessageListener.super.onMessage(data, acknowledgment);
    }

    @Override
    public void onMessage(@NonNull ConsumerRecord<K,V> data, @NonNull Consumer<?,?> consumer) {
        MessageListener.super.onMessage(data, consumer);
    }

    @Override
    public void onMessage(@NonNull ConsumerRecord<K,V> data, @Nullable Acknowledgment acknowledgment, @NonNull Consumer<?,?> consumer) {
        MessageListener.super.onMessage(data, acknowledgment, consumer);
    }
}
