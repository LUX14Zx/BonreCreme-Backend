package com.tlfdt.bonrecreme.service.bill.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ObjectMapper objectMapper;

    /**
     * Publishes an event when a bill has been successfully paid.
     *
     * @param billResponseDTO The details of the paid bill.
     */
    public void publishBillPaidEvent(BillResponseDTO billResponseDTO) {
        try {
            String billMessage = objectMapper.writeValueAsString(billResponseDTO);
            kafkaProducerService.sendMessage("paid-bills-topic", billMessage);
            log.info("Successfully published 'bill-paid' event for bill ID: {}", billResponseDTO.getBillId());
        } catch (JsonProcessingException e) {
            log.error("Error serializing paid bill DTO to JSON for bill ID: {}", billResponseDTO.getBillId(), e);
            // In a real application, might publish to a dead-letter queue here.
        }
    }
}