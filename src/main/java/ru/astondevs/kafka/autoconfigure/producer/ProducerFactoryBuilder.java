package ru.astondevs.kafka.autoconfigure.producer;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import ru.astondevs.kafka.autoconfigure.KafkaConfigurationProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Построитель для {@link ProducerFactory}.
 *
 * @author Ivan Andrianov
 * @author Maksim Yaskov
 *
 * @see ru.astondevs.kafka.autoconfigure.KafkaConfigurationProperties.ProducerProperties
 */
final class ProducerFactoryBuilder {

    /**
     * Возвращает новый построитель заполненный указанной конфигурацией.
     *
     * @param properties свойства конфигурации продюсера
     * @return новый построитель
     */
    static ProducerFactoryBuilder of(KafkaConfigurationProperties.ProducerProperties properties) {
        ProducerFactoryBuilder builder = ProducerFactoryBuilder.empty();
        if (properties == null) {
            return builder;
        }

        builder.property(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, StringUtils.collectionToCommaDelimitedString(properties.getBootstrapServers()))
                .property(ProducerConfig.BATCH_SIZE_CONFIG, (int) properties.getBatchSize().toBytes())
                .property(ProducerConfig.LINGER_MS_CONFIG, properties.getLingerMs());

        if (properties.getKeySerializer() != null) {
            builder.property(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, properties.getKeySerializer());
        }

        if (properties.getValueSerializer() != null) {
            builder.property(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, properties.getValueSerializer());
        }

        return builder;
    }

    /**
     * Возвращает пустой построитель.
     *
     * @return пустой построитель
     */
    static ProducerFactoryBuilder empty() {
        return new ProducerFactoryBuilder(new HashMap<>());
    }

    /**
     * Конфигурация для {@link DefaultKafkaProducerFactory}.
     */
    private final Map<String, Object> config;

    /**
     * Указывает нужно ли {@link DefaultKafkaProducerFactory} конфигурировать серелизаторы.
     */
    private boolean configureSerializers = true;

    /**
     * Серелизатор ключа.
     */
    private Serializer<?> keySerializer;

    /**
     * Серелизатор значения.
     */
    private Serializer<?> valueSerializer;

    private ProducerFactoryBuilder(Map<String, Object> config) {
        this.config = config;
    }

    /**
     * Устанавливает указанное свойство в конфигурацию.
     *
     * @param name названия свойства
     * @param value значения свойства
     * @return этот построитель
     * @see ProducerConfig
     */
    public ProducerFactoryBuilder property(String name, Object value) {
        config.put(name, value);
        return this;
    }

    /**
     * Применяет переданный {@link Consumer} к конфигурации.
     *
     * @param configConsumer потребитель конфигурации
     * @return этот построитель
     */
    public ProducerFactoryBuilder config(Consumer<Map<String, Object>> configConsumer) {
        configConsumer.accept(config);
        return this;
    }

    /**
     * Устанавливает серелизатор ключа.
     *
     * @param keySerializer серелизатор ключа
     * @return этот построитель
     */
    public ProducerFactoryBuilder keySerializer(@Nullable Serializer<?> keySerializer) {
        this.keySerializer = keySerializer;
        return this;
    }

    /**
     * Устанавливает серелизатор значения.
     *
     * @param valueSerializer серелизатор значения
     * @return этот построитель
     */
    public ProducerFactoryBuilder valueSerializer(@Nullable Serializer<?> valueSerializer) {
        this.valueSerializer = valueSerializer;
        return this;
    }

    /**
     * Устанавливает нужно ли {@link DefaultKafkaProducerFactory} конфигурировать серелизаторы.
     *
     * @param configureSerializers если false, то конфигурировать не нужно.
     * @return этот построитель
     */
    public ProducerFactoryBuilder configureSerializers(boolean configureSerializers) {
        this.configureSerializers = configureSerializers;
        return this;
    }

    /**
     * Создает новую {@link ProducerFactory}.
     *
     * @return новую фабрику
     */
    public ProducerFactory<?,?> build() {
        return new DefaultKafkaProducerFactory<>(config, keySerializer, valueSerializer, configureSerializers);
    }
}
