package ru.astondevs.kafka.autoconfigure;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.AbstractMessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.lang.NonNull;
import ru.astondevs.kafka.Event;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class KafkaConsumerBeanPostProcessor implements BeanPostProcessor {

    private final ConfigurableListableBeanFactory context;

    private final AutoConfigurationKafkaProperties properties;

    @Override
    public Object postProcessBeforeInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
        KafkaConsumer annotation = context.findAnnotationOnBean(beanName, KafkaConsumer.class);
        if (annotation == null) {
            return bean;
        }

        // check bean is AbstractKafkaConsumer

        AutoConfigurationKafkaProperties.ConsumerProperties consumerProperties = properties.getConsumers().get(annotation.value());

        var factory = new ConcurrentKafkaListenerContainerFactory<>();
        ConsumerFactory consumerFactory = consumerFactory(consumerProperties.getGroupId(), new StringDeserializer(), new JsonDeserializer<>(Event.class));
        factory.setConsumerFactory(consumerFactory);

        // add to map
        AbstractMessageListenerContainer container = factory.createContainer(consumerProperties.getTopic());
        container.setupMessageListener(bean);
        container.getContainerProperties().setGroupId(consumerProperties.getGroupId());
        container.start();

        return bean;
    }

    @PreDestroy
    public void destroy() {
        HashMap<Object, AbstractMessageListenerContainer> objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.forEach((k,v) -> v.stop());
    }

    public <K,V> ConsumerFactory<K, V> consumerFactory(String groupId, Deserializer<K> keyDeserializer, Deserializer<V> valueDeserializer) {
        return new DefaultKafkaConsumerFactory<>(Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092",
                ConsumerConfig.GROUP_ID_CONFIG, groupId,
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class,
                ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, "20971520",
                ConsumerConfig.FETCH_MAX_BYTES_CONFIG, "20971520"
        ), keyDeserializer, valueDeserializer);
    }
}
