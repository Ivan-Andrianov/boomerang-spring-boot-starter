package ru.astondevs.kafka.autoconfigure;

import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.unit.DataSize;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@ConfigurationProperties(prefix = "ru.astondevs.kafka")
public class AutoConfigurationKafkaProperties {

    private Map<String, ProducerProperties> producers;

    private Map<String, ConsumerProperties> consumers; // by groupId

    @Getter
    @Setter
    public static class ProducerProperties {

        private String topic;

        private DataSize batchSize = DataSize.ofKilobytes(16);

        private long lingerMs = 0;

        private List<String> bootstrapServers;

        private Class<?> keySerializer = StringSerializer.class;

        private Class<?> valueSerializer = JsonSerializer.class;

    }

    @Getter
    @Setter
    public static class ConsumerProperties {

        private String topic;

        private String groupId;

        private DataSize batchSize = DataSize.ofKilobytes(16);

        private long lingerMs = 0;

        private List<String> bootstrapServers;

        private Class<?> keySerializer = StringSerializer.class;

        private Class<?> valueSerializer = JsonSerializer.class;

        private String keyDeserializerBeanName;

        private String valueDeserializerBeanName;

    }


}
