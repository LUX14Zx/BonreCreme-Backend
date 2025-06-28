package com.tlfdt.bonrecreme.config.message.kafka;

import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

/**
 * Unified configuration for Kafka consumers. The producer configuration is
 * now handled entirely by Spring Boot's auto-configuration based on the
 * properties in application.yml.
 */
@Configuration
public class KafkaConfig {

    /**
     * Creates the Kafka ConsumerFactory.
     * It uses the properties defined in application.yml, including the bootstrap servers, group ID,
     * and trusted packages for JSON deserialization.
     *
     * @param properties The auto-configured Kafka properties from Spring Boot.
     * @return A ConsumerFactory for creating Kafka consumers.
     */
    @Bean
    public ConsumerFactory<String, Object> consumerFactory(KafkaProperties properties) {
        // Spring Boot automatically picks up consumer properties from application.yml,
        // including the crucial 'spring.json.trusted.packages'.
        return new DefaultKafkaConsumerFactory<>(properties.buildConsumerProperties());
    }

    /**
     * Creates the container factory for Kafka listeners.
     * This factory is used by the @KafkaListener annotation to create message consumers.
     *
     * @param consumerFactory The configured ConsumerFactory.
     * @return A ConcurrentKafkaListenerContainerFactory.
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
            ConsumerFactory<String, Object> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }

    // The ProducerFactory and KafkaTemplate beans have been removed.
    // Spring Boot will auto-configure them based on your application.yml settings,
    // which is the recommended approach. You can directly @Autowired KafkaTemplate
    // in your services without needing to define it here.
}
