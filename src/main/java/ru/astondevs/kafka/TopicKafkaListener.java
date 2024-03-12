package ru.astondevs.kafka;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;

@KafkaListener(topics = "topic", containerFactory = "containerFactory")
public class TopicKafkaListener {

    @KafkaHandler
    public void handle(Event event) {
        // handle
    }

}
