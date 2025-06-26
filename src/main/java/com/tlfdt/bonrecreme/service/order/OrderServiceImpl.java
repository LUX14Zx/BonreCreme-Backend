package com.tlfdt.bonrecreme.service.order;

import com.tlfdt.bonrecreme.controller.api.v1.customer.dto.MenuRequestDTO;
import com.tlfdt.bonrecreme.controller.api.v1.kitchen.dto.OrderNotificationDTO;
import com.tlfdt.bonrecreme.controller.api.v1.kitchen.dto.UpdateOrderRequestDTO;
import com.tlfdt.bonrecreme.exception.custom.CustomExceptionHandler;
import com.tlfdt.bonrecreme.model.restaurant.MenuItem;
import com.tlfdt.bonrecreme.model.restaurant.Order;
import com.tlfdt.bonrecreme.model.restaurant.OrderItem;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tlfdt.bonrecreme.service.order.masseging.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.tlfdt.bonrecreme.model.restaurant.SeatTable;
import com.tlfdt.bonrecreme.model.restaurant.enums.OrderStatus;
import com.tlfdt.bonrecreme.repository.restaurant.MenuItemRepository;
import com.tlfdt.bonrecreme.repository.restaurant.OrderItemRepository;
import com.tlfdt.bonrecreme.repository.restaurant.OrderRepository;
import com.tlfdt.bonrecreme.repository.restaurant.SeatTableRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl.class);
    private final MenuItemRepository menuItemRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final SeatTableRepository seatTableRepository;
    private final KafkaProducerService kafkaProducerService;
    private final ObjectMapper objectMapper; //

    /**
     * Creates a new order, including finding the table, creating the order items,
     * and saving everything to the database within a single transaction.
     *
     * @param menuRequestDTO DTO with order details.
     * @return The complete Order object with all its items.
     */
    @Override
    @Transactional("restaurantTransactionManager")
    public Order createOrder(MenuRequestDTO menuRequestDTO) {
        // Step 1: Validate that the table exists. Throws ResourceNotFoundException if not found.
        Optional<SeatTable> table = seatTableRepository.findById(menuRequestDTO.getTableId());

        Order order = new Order();
        if (table.isPresent())
        {
            order.setSeatTable(table.get());
            order.setStatus(OrderStatus.NEW);
        }
        Order savedOrder = orderRepository.save(order);

        // Step 3: Process and create the list of OrderItem entities.
        List<OrderItem> orderItems = menuRequestDTO.getItems().stream()
                .map(itemRequest -> {
                    // Find the corresponding menu item. Throws ResourceNotFoundException if not found.
                    Optional<MenuItem> menuItem = menuItemRepository.findById(itemRequest.getMenuItemId());

                    // Create the order item and link it to the parent order and menu item.
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(savedOrder);
                    orderItem.setMenuItem(menuItem.get());
                    orderItem.setQuantity(itemRequest.getQuantity());
                    orderItem.setSpecialRequests(itemRequest.getSpecialRequests());
                    return orderItem;
                })
                .collect(Collectors.toList());

        // Step 4: Batch save all order items for efficiency.
        orderItemRepository.saveAll(orderItems);

        // Step 5: Set the saved items back to the order object to return the complete entity.
        savedOrder.setOrderItems(orderItems);

        // Create the DTO from the saved order
        OrderNotificationDTO notificationDTO = OrderNotificationDTO.fromOrder(savedOrder);

        // Serialize the DTO to a JSON string and send to Kafka
        try {
            String orderJson = objectMapper.writeValueAsString(notificationDTO);
            kafkaProducerService.sendMessage("order-topic", orderJson);
        } catch (Exception e) {
            LOGGER.error("Failed to serialize order notification or send to Kafka", e);
            // Decide how to handle this error. Maybe a fallback mechanism?
        }

        return savedOrder;
    }


    @Transactional("restaurantTransactionManager")
    @Override
    public Order updateOrder(Long orderId, UpdateOrderRequestDTO requestDTO) {
        // Step 1: Find the existing order
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomExceptionHandler("Order not found with id: " + orderId));

        // Step 2: Clear the old order items
        orderItemRepository.deleteAll(order.getOrderItems());
        order.getOrderItems().clear();

        // Step 3: Create the new list of order items from the request
        List<OrderItem> updatedItems = requestDTO.getItems().stream()
                .map(itemRequest -> {
                    MenuItem menuItem = menuItemRepository.findById(itemRequest.getMenuItemId())
                            .orElseThrow(() -> new CustomExceptionHandler("MenuItem not found with id: " + itemRequest.getMenuItemId()));
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(order);
                    orderItem.setMenuItem(menuItem);
                    orderItem.setQuantity(itemRequest.getQuantity());
                    orderItem.setSpecialRequests(itemRequest.getSpecialRequests());
                    return orderItem;
                })
                .collect(Collectors.toList());

        // Step 4: Save the new items and update the order
        orderItemRepository.saveAll(updatedItems);
        order.setOrderItems(updatedItems);
        order.setStatus(OrderStatus.UPDATE); // Update status to reflect the change
        Order savedOrder = orderRepository.save(order);

        // Step 5: Send the "update-order" event to Kafka
        OrderNotificationDTO notificationDTO = OrderNotificationDTO.fromOrder(savedOrder);
        try {
            String orderJson = objectMapper.writeValueAsString(notificationDTO);
            kafkaProducerService.sendMessage("update-order-topic", orderJson);
        } catch (Exception e) {
            LOGGER.error("Failed to serialize order update notification or send to Kafka", e);
        }

        return savedOrder;
    }
}
