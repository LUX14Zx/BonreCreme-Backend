package com.tlfdt.bonrecreme.service.order.massaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * A generic, type-safe service for producing messages to Kafka topics.
 * <p>
 * This service leverages Spring's configured {@link KafkaTemplate} to automatically
 * serialize Java objects to JSON. It also provides robust, asynchronous error
 * handling for all outgoing messages.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    /**
     * The KafkaTemplate is configured to handle Object serialization.
     */
    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Sends a payload to a specified Kafka topic asynchronously.
     * <p>
     * The method automatically serializes the payload object to JSON. It handles the
     * asynchronous response from Kafka, logging success or failure appropriately.
     *
     * @param topic   The target Kafka topic.
     * @param payload The object to be sent as the message payload.
     * @param <T>     The type of the payload object.
     */
    public <T> void sendMessage(String topic, T payload) {
        log.info("Attempting to send message to topic: {}", topic);

        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, payload);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                // Success
                log.info("Successfully sent message to topic [{}], partition [{}], offset [{}]",
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            } else {
                // Failure
                log.error("Failed to send message to topic [{}]. Error: {}",
                        topic,
                        ex.getMessage());
                // In a production system, you might add logic here to send the failed message
                // to a Dead Letter Queue (DLQ) or trigger an alert.
            }
        });
    }
}