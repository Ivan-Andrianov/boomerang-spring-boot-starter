package ru.astondevs.kafka.autoconfigure;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация, которая помечает bean, как потребителя сообщений из kafka.
 * Тип bean'а помеченного данной аннотацией должен расширять {@link AbstractKafkaConsumer}.
 *
 * @author Максим Яськов
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
