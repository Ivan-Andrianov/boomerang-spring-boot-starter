package ru.astondevs.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import ru.astondevs.kafka.autoconfigure.KafkaProducer;

@KafkaProducer("topic-producer") // название продюсера в конфигурации
public class TopicKafkaProducer extends AbstractKafkaProducer<Long, Event> {

    public TopicKafkaProducer(KafkaTemplate<Long, Event> kafkaTemplate) {
        super(kafkaTemplate);
    }

}
