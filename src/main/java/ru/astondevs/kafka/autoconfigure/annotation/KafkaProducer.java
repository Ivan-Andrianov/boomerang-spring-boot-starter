package ru.astondevs.kafka.autoconfigure.annotation;

import org.springframework.stereotype.Component;
import ru.astondevs.kafka.autoconfigure.producer.AbstractKafkaProducer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация, которая помечает bean, как продюсера сообщений в kafka.
 * Тип bean'а помеченного данной аннотацией должен расширять {@link AbstractKafkaProducer}.
 *
 * @author Ivan Andrianov
 * @author Maksim Yaskov
 */
@Component
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface KafkaProducer {

    /**
     * @return название конфигурации для данного продюсера
     */
    String value() default "";

}
