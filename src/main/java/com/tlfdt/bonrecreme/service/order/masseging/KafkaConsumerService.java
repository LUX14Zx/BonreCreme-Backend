package com.tlfdt.bonrecreme.service.order.masseging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tlfdt.bonrecreme.controller.api.v1.kitchen.KitchenSseController;
import com.tlfdt.bonrecreme.controller.api.v1.kitchen.dto.OrderNotificationDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerService.class);
    private final KitchenSseController kitchenSseController;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "order-topic", groupId = "kitchen-group")
    public void consumeOrder(String message) {
        try {
            OrderNotificationDTO order = objectMapper.readValue(message, OrderNotificationDTO.class);
            LOGGER.info("Consumed order: {}", order);
            kitchenSseController.sendOrderToKitchen(order);
        } catch (Exception err) {
            LOGGER.error("Failed to deserialize order message or send to kitchen", err);
        }
    }

    @KafkaListener(topics = "update-order-topic", groupId = "kitchen-group")
    public void consumeOrderUpdate(String message) {
        try {
            OrderNotificationDTO order = objectMapper.readValue(message, OrderNotificationDTO.class);
            LOGGER.info("Consumed order update: {}", order);
            kitchenSseController.sendOrderUpdateToKitchen(order);
        } catch (Exception err) {
            LOGGER.error("Failed to deserialize order update message or send to kitchen", err);
        }
    }
}