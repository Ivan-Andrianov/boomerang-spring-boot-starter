package ru.astondevs.kafka.autoconfigure;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.support.Acknowledgment;
import ru.astondevs.kafka.Event;

public abstract class AbstractKafkaConsumer<K,V> implements MessageListener<String, Event> {

    @Override
    public abstract void onMessage(ConsumerRecord<String, Event> data);

    @Override
    public void onMessage(ConsumerRecord<String, Event> data, Acknowledgment acknowledgment) {
        MessageListener.super.onMessage(data, acknowledgment);
    }

    @Override
    public void onMessage(ConsumerRecord<String, Event> data, Consumer<?, ?> consumer) {
        MessageListener.super.onMessage(data, consumer);
    }

    @Override
    public void onMessage(ConsumerRecord<String, Event> data, Acknowledgment acknowledgment, Consumer<?, ?> consumer) {
        MessageListener.super.onMessage(data, acknowledgment, consumer);
    }
}
