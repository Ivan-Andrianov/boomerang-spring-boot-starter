# boomerang-kafka-spring-boot-starter
Автоматическая конфигурация необходимых компонентов для работы с Kafka.

## How to use
Пример `application.yml`:
```yaml
ru.astondevs.kafka:
      producers:
        some-topic-producer: # название конфигурации для продюсера
          topic: 'some-topic' # название топика продюсера
          bootstrap-servers: # список серверов kafka хост:порт
            - 'localhost:29092'
          batch-size: '16Kb' # размер пакетов
          linger-ms: 0 # задержка перед отправкой пакета
          key-serializer: # класс серелизатора ключа (по умолчанию StringSerializer.class)
          key-serializer-bean-name: # названия бина - серелизатора ключа
          value-serializer: # класс серелизатора значения (по умолчанию JsonSerializer.class)
          value-serializer-bean-name: # названия бина - серелизатора значения
      consumers:
        some-topic-consumer: # название конфигурации для потребителя
          topic: 'some-topic' # название топика потребителя
          bootstrap-servers: # список серверов kafka хост:порт
            - 'localhost:29092'
          group-id: 'groupId' # идентификатор группы потребителя
          key-deserializer: # класс десерелизатора ключа (по умолчанию StringDeserializer.class)
          key-deserializer-bean-name: # названия бина - десерелизатора ключа
          value-deserializer: # класс десерелизатора значения (по умолчанию JsonDeserializer.class)
          value-deserializer-bean-name: # названия бина - десерелизатора значения
```

Пример определения компонента продюсера:
```java
@KafkaProducer(config = "some-topic-producer") // название конфигурации в пропертях
public class SomeTopicKafkaProducer extends AbstractKafkaProducer<String, Event> {

}
```

Пример использования продюсера:
```java
@Bean
@RequiredArgsConstructor
public class KafkaRunner implements CommandLineRunner {

    private final SomeTopicKafkaProducer kafkaProducer;
    
    @Override
    public void run(String... args) throws Exception {
        kafkaProducer.send(UUID.randomUUID().toString(), new Event("dto-value"))
                .whenComplete((stringDtoSendResult, throwable) -> {
                    // some handle
                });
    }
}
```

Пример определения компонента потребителя:
```java
@KafkaConsumer(config = "some-topic-consumer") // название конфигурации в пропертях
public class SomeTopicConsumerListener extends AbstractKafkaConsumer<String, Event> {

    @Override
    public void onMessage(ConsumerRecord<String, Event> data) {
        // consume here
    }
}
```

Пример определения десерелизатора:
```java
@Configuration
public class ApplicationConfiguration {
    
    @Bean
    // использовать eventJsonDeserializer в проперти как beanName
    public JsonDeserializer<Event> eventJsonDeserializer() {
        return new JsonDeserializer<>(Event.class);
    }
}
```