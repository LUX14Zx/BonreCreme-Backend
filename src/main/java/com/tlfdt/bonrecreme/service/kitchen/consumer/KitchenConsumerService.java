package com.tlfdt.bonrecreme.service.kitchen.consumer;

import com.tlfdt.bonrecreme.service.kitchen.KitchenSseService;
import com.tlfdt.bonrecreme.controller.api.v1.kitchen.dto.OrderNotificationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KitchenConsumerService {

    private final KitchenSseService kitchenSseService;
    /**
     * Listens for new order events from the "order-topic" on Kafka.
     * The listener is configured to automatically deserialize the JSON message
     * into an OrderNotificationDTO object.
     *
     * @param order The deserialized order notification from Kafka.
     */
    @KafkaListener(topics = "order-topic", groupId = "kitchen-group")
    public void consumeNewOrder(OrderNotificationDTO order) {
        log.info("Consumed new order event from Kafka for Order ID {}", order.getOrderId());
        kitchenSseService.sendEventToAllEmitters("new-order", order);
    }

    /**
     * Listens for order update events from the "update-order-topic" on Kafka.
     *
     * @param order The deserialized order notification from Kafka.
     */
    @KafkaListener(topics = "update-order-topic", groupId = "kitchen-group")
    public void consumeOrderUpdate(OrderNotificationDTO order) {
        log.info("Consumed order update event from Kafka for Order ID {}", order.getOrderId());
        kitchenSseService.sendEventToAllEmitters("update-order", order);
    }

    //TODO
    @KafkaListener(topics = "customer-order-update-topic", groupId = "kitchen-group")
    public void consumeCustomerOrderUpdate(OrderNotificationDTO order) {
        log.info("Consumed Customer order update event from Kafka for Order ID {}", order.getOrderId());
        kitchenSseService.sendEventToAllEmitters("customer-order-update", order);
    }
}
