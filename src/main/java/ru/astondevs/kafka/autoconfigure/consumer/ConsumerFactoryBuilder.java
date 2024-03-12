package ru.astondevs.kafka.autoconfigure.consumer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.util.StringUtils;
import ru.astondevs.kafka.autoconfigure.KafkaConfigurationProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Построитель для {@link ConsumerFactory}.
 *
 * @author Ivan Andrianov
 * @author Maksim Yaskov
 *
 * @see ru.astondevs.kafka.autoconfigure.KafkaConfigurationProperties.ConsumerProperties
 */
final class ConsumerFactoryBuilder {

    /**
     * Возвращает новый построитель заполненный указанной конфигурацией.
     *
     * @param properties свойства конфигурации потребителя
     * @return новый построитель
     */
    static ConsumerFactoryBuilder of(KafkaConfigurationProperties.ConsumerProperties properties) {
        ConsumerFactoryBuilder builder = ConsumerFactoryBuilder.empty();
        if (properties == null) {
            return builder;
        }

        builder.property(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, StringUtils.collectionToCommaDelimitedString(properties.getBootstrapServers()))
                .property(ConsumerConfig.GROUP_ID_CONFIG, properties.getGroupId());

        if (properties.getKeyDeserializer() != null) {
            builder.property(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, properties.getKeyDeserializer());
        }

        if (properties.getValueDeserializer() != null) {
            builder.property(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, properties.getValueDeserializer());
        }

        return builder;
    }

    /**
     * Возвращает пустой построитель.
     *
     * @return пустой построитель
     */
    static ConsumerFactoryBuilder empty() {
        return new ConsumerFactoryBuilder(new HashMap<>());
    }

    /**
     * Конфигурация для {@link DefaultKafkaConsumerFactory}.
     */
    private final Map<String, Object> config;

    /**
     * Указывает нужно ли {@link DefaultKafkaConsumerFactory} конфигурировать десерелизаторы.
     */
    private boolean configureDeserializers = true;

    /**
     * Десерелизатор ключа.
     */
    private Deserializer<?> keyDeserializer;

    /**
     * Десерелизатор ключа.
     */
    private Deserializer<?> valueDeserializer;

    private ConsumerFactoryBuilder(Map<String, Object> config) {
        this.config = config;
    }

    /**
     * Устанавливает указанное свойство в конфигурацию.
     *
     * @param name названия свойства
     * @param value значения свойства
     * @return этот построитель
     * @see ConsumerConfig
     */
    public ConsumerFactoryBuilder property(String name, Object value) {
        config.put(name, value);
        return this;
    }

    /**
     * Применяет переданный {@link Consumer} к конфигурации.
     *
     * @param configConsumer потребитель конфигурации
     * @return этот построитель
     */
    public ConsumerFactoryBuilder config(Consumer<Map<String, Object>> configConsumer) {
        configConsumer.accept(config);
        return this;
    }

    /**
     * Устанавливает десерелизатор ключа.
     *
     * @param keyDeserializer десерелизатор ключа
     * @return этот построитель
     */
    public ConsumerFactoryBuilder keyDeserializer(Deserializer<?> keyDeserializer) {
        this.keyDeserializer = keyDeserializer;
        return this;
    }

    /**
     * Устанавливает десерелизатор значения.
     *
     * @param valueDeserializer десерелизатор значения
     * @return этот построитель
     */
    public ConsumerFactoryBuilder valueDeserializer(Deserializer<?> valueDeserializer) {
        this.valueDeserializer = valueDeserializer;
        return this;
    }

    /**
     * Устанавливает нужно ли {@link DefaultKafkaConsumerFactory} конфигурировать десерелизаторы.
     *
     * @param configureDeserializers если false, то конфигурировать не нужно.
     * @return этот построитель
     */
    public ConsumerFactoryBuilder configureDeserializers(boolean configureDeserializers) {
        this.configureDeserializers = configureDeserializers;
        return this;
    }

    /**
     * Создает новую {@link ConsumerFactory}.
     *
     * @return новую фабрику
     */
    public ConsumerFactory<?,?> build() {
        return new DefaultKafkaConsumerFactory<>(config, keyDeserializer, valueDeserializer, configureDeserializers);
    }
}
