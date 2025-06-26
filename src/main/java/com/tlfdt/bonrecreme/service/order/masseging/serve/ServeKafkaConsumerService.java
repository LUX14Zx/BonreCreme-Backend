package com.tlfdt.bonrecreme.service.order.masseging.serve;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tlfdt.bonrecreme.controller.api.v1.kitchen.dto.OrderNotificationDTO;
import com.tlfdt.bonrecreme.controller.api.v1.waitstaff.WaitstaffMonitorSse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ServeKafkaConsumerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServeKafkaConsumerService.class);
    private final WaitstaffMonitorSse waitstaffMonitorSse;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "serve-order-topic", groupId = "waitstaff-group")
    public void consumeReadyToServeOrder(String message) {
        try {
            OrderNotificationDTO order = objectMapper.readValue(message, OrderNotificationDTO.class);
            LOGGER.info("Consumed ready to serve order: {}", order);
            waitstaffMonitorSse.sendOrderToWaitstaff(order);
        } catch (Exception err) {
            LOGGER.error("Failed to deserialize ready to serve order message or send to waitstaff", err);
        }
    }
}