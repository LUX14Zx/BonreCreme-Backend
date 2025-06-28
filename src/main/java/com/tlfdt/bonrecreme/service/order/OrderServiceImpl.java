package com.tlfdt.bonrecreme.service.order;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tlfdt.bonrecreme.controller.api.v1.customer.dto.MenuRequestDTO;
import com.tlfdt.bonrecreme.controller.api.v1.kitchen.dto.OrderNotificationDTO;
import com.tlfdt.bonrecreme.controller.api.v1.kitchen.dto.UpdateOrderRequestDTO;
import com.tlfdt.bonrecreme.controller.api.v1.kitchen.dto.UpdateOrderStatusRequestDTO;
import com.tlfdt.bonrecreme.exception.custom.CustomExceptionHandler;
import com.tlfdt.bonrecreme.exception.resource.ResourceNotFoundException;
import com.tlfdt.bonrecreme.model.restaurant.MenuItem;
import com.tlfdt.bonrecreme.model.restaurant.Order;
import com.tlfdt.bonrecreme.model.restaurant.OrderItem;
import com.tlfdt.bonrecreme.model.restaurant.SeatTable;
import com.tlfdt.bonrecreme.model.restaurant.enums.OrderStatus;
import com.tlfdt.bonrecreme.repository.restaurant.MenuItemRepository;
import com.tlfdt.bonrecreme.repository.restaurant.OrderRepository;
import com.tlfdt.bonrecreme.repository.restaurant.SeatTableRepository;
import com.tlfdt.bonrecreme.service.order.massaging.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for managing order-related business logic.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final MenuItemRepository menuItemRepository;
    private final OrderRepository orderRepository;
    private final SeatTableRepository seatTableRepository;
    private final KafkaProducerService kafkaProducerService;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional("restaurantTransactionManager")
    public OrderNotificationDTO createOrder(MenuRequestDTO menuRequestDTO) {
        SeatTable table = seatTableRepository.findById(menuRequestDTO.getTableId())
                .orElseThrow(() -> new ResourceNotFoundException("SeatTable not found with id: " + menuRequestDTO.getTableId()));

        // Use the builder to create the new Order instance
        Order order = Order.builder()
                .seatTable(table)
                .status(OrderStatus.PENDING)
                .build();
        // The order must be saved first to get an ID before orderItems can reference it.
        Order savedOrder = orderRepository.save(order);

        List<OrderItem> orderItems = createOrderItemsForOrder(menuRequestDTO.getItems(), savedOrder);
        order.setOrderItems(new HashSet<>(orderItems)); // Convert the list to a HashSet

        OrderNotificationDTO notificationDTO = OrderNotificationDTO.fromOrder(order);
        publishOrderNotification("order-topic", notificationDTO);

        log.info("Successfully created Order #{} for table #{}", savedOrder.getId(), table.getTableNumber());
        return notificationDTO;
    }

    @Override
    @Transactional("restaurantTransactionManager")
    public OrderNotificationDTO updateOrderItems(Long orderId, UpdateOrderRequestDTO requestDTO) {
        Order order = findOrderById(orderId);

        // This is the key line for removing old items
        order.getOrderItems().clear();

        List<OrderItem> updatedItems = createOrderItemsForOrder(requestDTO.getItems(), order);
        order.getOrderItems().addAll(updatedItems);

        order.setStatus(OrderStatus.PENDING); // Reset status to PENDING after update
        Order savedOrder = orderRepository.save(order);

        OrderNotificationDTO notificationDTO = OrderNotificationDTO.fromOrder(savedOrder);
        publishOrderNotification("update-order-topic", notificationDTO);

        log.info("Successfully updated items for Order #{}", savedOrder.getId());
        return notificationDTO;
    }

    @Override
    @Transactional("restaurantTransactionManager")
    public OrderNotificationDTO updateOrderStatus(Long orderId, UpdateOrderStatusRequestDTO requestDTO) {
        Order order = findOrderById(orderId);

        OrderStatus newStatus = OrderStatus.fromDisplayName(String.valueOf(requestDTO.getStatus()));
        order.setStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);

        OrderNotificationDTO notificationDTO = OrderNotificationDTO.fromOrder(updatedOrder);
        publishOrderNotification("update-order-topic", notificationDTO);

        log.info("Updated status of Order #{} to {}", updatedOrder.getId(), updatedOrder.getStatus());
        return notificationDTO;
    }

    @Override
    @Transactional("restaurantTransactionManager")
    public OrderNotificationDTO markOrderAsReadyToServe(Long orderId) {
        Order order = findOrderById(orderId);
        order.setStatus(OrderStatus.READY_TO_SERVE);
        Order updatedOrder = orderRepository.save(order);

        OrderNotificationDTO notificationDTO = OrderNotificationDTO.fromOrder(updatedOrder);
        // Publish to a specific topic for waitstaff
        publishOrderNotification("serve-order-topic", notificationDTO);

        log.info("Marked Order #{} as READY_TO_SERVE", updatedOrder.getId());
        return notificationDTO;
    }

    @Override
    @Transactional("restaurantTransactionManager")
    public OrderNotificationDTO markOrderAsServed(Long orderId) {
        Order order = findOrderById(orderId);
        order.setStatus(OrderStatus.SERVED);
        Order updatedOrder = orderRepository.save(order);

        OrderNotificationDTO notificationDTO = OrderNotificationDTO.fromOrder(updatedOrder);
        publishOrderNotification("update-order-topic", notificationDTO);

        log.info("Marked Order #{} as SERVED", updatedOrder.getId());
        return notificationDTO;
    }

    /**
     * Private helper to find an Order by its ID, providing a consistent exception.
     *
     * @param orderId The ID of the order to find.
     * @return The found Order entity.
     * @throws ResourceNotFoundException if the order is not found.
     */
    private Order findOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
    }

    /**
     * Helper method to convert OrderItemRequest DTOs into OrderItem entities.
     *
     * @param itemRequests The list of requested items.
     * @param order        The parent order for these items.
     * @return A list of new, unsaved OrderItem entities.
     */
    private List<OrderItem> createOrderItemsForOrder(List<MenuRequestDTO.OrderItemRequest> itemRequests, Order order) {
        return itemRequests.stream()
                .map(itemDto -> {
                    MenuItem menuItem = menuItemRepository.findById(itemDto.getMenuItemId())
                            .orElseThrow(() -> new ResourceNotFoundException("menuitems not found with id: " + itemDto.getMenuItemId()));

                    return OrderItem.builder()
                            .order(order)
                            .menuItem(menuItem)
                            .quantity(itemDto.getQuantity())
                            .priceAtTime(menuItem.getPrice()) // Capture the price at the time of order
                            .specialRequests(itemDto.getSpecialRequests())
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * Publishes an order notification to the specified Kafka topic.
     *
     * @param topic   The target Kafka topic.
     * @param payload The notification DTO to send.
     */
    private void publishOrderNotification(String topic, OrderNotificationDTO payload) {
        try {
            // Remove the manual serialization to a string
            // String message = objectMapper.writeValueAsString(payload);

            // Send the payload object directly. The Kafka producer will handle serialization.
            kafkaProducerService.sendMessage(topic, payload);
        } catch (Exception e) {
            log.error("Failed to send notification for Order ID: {}", payload.getOrderId(), e);
            throw new CustomExceptionHandler("Failed to send order notification.");
        }
    }
}
