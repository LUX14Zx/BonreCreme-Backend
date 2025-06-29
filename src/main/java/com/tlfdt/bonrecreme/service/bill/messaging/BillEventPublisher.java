package com.tlfdt.bonrecreme.service.bill.messaging;

import com.tlfdt.bonrecreme.controller.api.v1.cashier.dto.bill.BillResponseDTO;
import com.tlfdt.bonrecreme.config.message.kafka.producer.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Publishes bill-related events to message queues (e.g., Kafka).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BillEventPublisher {

    private final KafkaProducerService kafkaProducerService;
    // The ObjectMapper is no longer needed here as serialization is handled by Kafka's JsonSerializer.

    /**
     * Publishes an event when a bill has been successfully paid.
     *
     * @param billResponseDTO The details of the paid bill.
     */
    public void publishBillPaidEvent(BillResponseDTO billResponseDTO) {
        try {
            // Send the DTO object directly. Spring Kafka's JsonSerializer will handle serialization.
            kafkaProducerService.sendMessage("paid-bills-topic", billResponseDTO);
            log.info("Published 'bill-paid' event for bill ID: {}", billResponseDTO.getBillId());
        } catch (Exception e) {
            log.error("Error publishing 'bill-paid' event for bill ID: {}", billResponseDTO.getBillId(), e);
            // In a real application, you might publish to a dead-letter queue here.
        }
    }
}