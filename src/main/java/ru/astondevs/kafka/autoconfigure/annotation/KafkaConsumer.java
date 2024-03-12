package ru.astondevs.kafka.autoconfigure.annotation;

import org.springframework.stereotype.Component;
import ru.astondevs.kafka.autoconfigure.consumer.AbstractKafkaConsumer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация, которая помечает bean, как потребителя сообщений из kafka.
 * Тип bean'а помеченного данной аннотацией должен расширять {@link AbstractKafkaConsumer}.
 *
 * @author Ivan Andrianov
 * @author Maksim Yaskov
 */
@Component
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface KafkaConsumer {

    /**
     * @return название конфигурации для данного потребителя
     */
    String value();

}
