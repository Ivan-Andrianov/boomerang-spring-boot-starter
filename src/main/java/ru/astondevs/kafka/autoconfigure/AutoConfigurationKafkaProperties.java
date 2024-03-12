package ru.astondevs.kafka.autoconfigure;

import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.unit.DataSize;

import java.util.List;
import java.util.Map;

/**
 * Свойства автоматической конфигурации.
 *
 * @author Ivan Andrianov
 * @author Maksim Yaskov
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "ru.astondevs.kafka")
public class AutoConfigurationKafkaProperties {

    /**
     * Карта названия конфигурации продюсера и свойства конфигурации продюсера.
     */
    private Map<String, ProducerProperties> producers;

    /**
     * Карта названия конфигурации потребителя и свойства конфигурации потребителя.
     */
    private Map<String, ConsumerProperties> consumers;

    /**
     * Свойства kafka продюсера.
     */
    @Getter
    @Setter
    public static class ProducerProperties {

        /**
         * Размер пакета по умолчанию.
         */
        private DataSize batchSize = DataSize.ofKilobytes(16);

        /**
         * Разделенный запятыми список пар хост:порт,
         * которые будут использоваться для установления начальных подключений к кластеру Kafka.
         */
        private List<String> bootstrapServers;

        /**
         * Название топика.
         */
        private String topic;

        /**
         * Задержка перед отправкой пакета (миллисекунды).
         */
        private long lingerMs = 0;

        /**
         * Класс серелизатора ключа.
         */
        private Class<?> keySerializer = StringSerializer.class;

        /**
         * Название компонента являющегося серелизатором ключа.
         */
        private String keySerializerBeanName;

        /**
         * Класс серелизатора значения.
         */
        private Class<?> valueSerializer = JsonSerializer.class;

        /**
         * Название компонента являющегося серелизатором значения.
         */
        private String valueSerializerBeanName;

    }

    /**
     * Свойства kafka потребителя.
     */
    @Getter
    @Setter
    public static class ConsumerProperties {

        /**
         * Разделенный запятыми список пар хост:порт,
         * которые будут использоваться для установления начальных подключений к кластеру Kafka.
         */
        private List<String> bootstrapServers;

        /**
         * Название топика.
         */
        private String topic;

        /**
         * Идентификатор группы потребителей.
         */
        private String groupId;

        /**
         * Класс серелизатора ключа.
         */
        private Class<?> keySerializer = StringSerializer.class;

        /**
         * Название компонента являющегося серелизатором ключа.
         */
        private String keyDeserializerBeanName;

        /**
         * Название компонента являющегося серелизатором значения.
         */
        private String valueDeserializerBeanName;

        /**
         * Класс серелизатора значения.
         */
        private Class<?> valueSerializer = JsonSerializer.class;

    }
}
