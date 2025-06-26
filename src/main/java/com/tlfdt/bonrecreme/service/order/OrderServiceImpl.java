package com.tlfdt.bonrecreme.service.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tlfdt.bonrecreme.controller.api.v1.customer.dto.MenuRequestDTO;
import com.tlfdt.bonrecreme.controller.api.v1.kitchen.dto.OrderNotificationDTO;
import com.tlfdt.bonrecreme.controller.api.v1.kitchen.dto.UpdateOrderRequestDTO;
import com.tlfdt.bonrecreme.controller.api.v1.kitchen.dto.UpdateOrderStatusRequestDTO;
import com.tlfdt.bonrecreme.exception.custom.CustomExceptionHandler;
import com.tlfdt.bonrecreme.model.restaurant.*;
import com.tlfdt.bonrecreme.model.restaurant.enums.OrderStatus;
import com.tlfdt.bonrecreme.repository.restaurant.*;
import com.tlfdt.bonrecreme.service.order.masseging.KafkaProducerService;
import com.tlfdt.bonrecreme.utils.bill.GetBill;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
    private final BillRepository billRepository;
    private final KafkaProducerService kafkaProducerService;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional("restaurantTransactionManager")
    public OrderNotificationDTO createOrder(MenuRequestDTO menuRequestDTO) {
        Optional<SeatTable> table = seatTableRepository.findById(menuRequestDTO.getTableId());

        Order order = new Order();
        if (table.isPresent()) {
            order.setSeatTable(table.get());
            order.setStatus(OrderStatus.NEW);
        }
        Order savedOrder = orderRepository.save(order);

        List<OrderItem> orderItems = menuRequestDTO.getItems().stream()
                .map(itemRequest -> {
                    Optional<MenuItem> menuItem = menuItemRepository.findById(itemRequest.getMenuItemId());

                    OrderItem orderItem = new OrderItem();
                    if (menuItem.isPresent()) {
                        orderItem.setOrder(savedOrder);
                        orderItem.setMenuItem(menuItem.get());
                        orderItem.setQuantity(itemRequest.getQuantity());
                        orderItem.setSpecialRequests(itemRequest.getSpecialRequests());
                    }
                    return orderItem;
                })
                .collect(Collectors.toList());

        orderItemRepository.saveAll(orderItems);
        savedOrder.setOrderItems(orderItems);
        OrderNotificationDTO notificationDTO = OrderNotificationDTO.fromOrder(savedOrder);

        try {
            String orderJson = objectMapper.writeValueAsString(notificationDTO);
            kafkaProducerService.sendMessage("order-topic", orderJson);
        } catch (Exception e) {
            LOGGER.error("Failed to serialize order notification or send to Kafka", e);
        }

        return notificationDTO;
    }

    @Transactional("restaurantTransactionManager")
    @Override
    public OrderNotificationDTO updateOrder(Long orderId, UpdateOrderRequestDTO requestDTO) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomExceptionHandler("Order not found with id: " + orderId));

        // Clear the existing collection and add the new items
        order.getOrderItems().clear();
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
        order.getOrderItems().addAll(updatedItems);

        order.setStatus(OrderStatus.UPDATE);
        Order savedOrder = orderRepository.save(order);

        OrderNotificationDTO notificationDTO = OrderNotificationDTO.fromOrder(savedOrder);
        try {
            String orderJson = objectMapper.writeValueAsString(notificationDTO);
            kafkaProducerService.sendMessage("update-order-topic", orderJson);
        } catch (Exception e) {
            LOGGER.error("Failed to serialize order update notification or send to Kafka", e);
        }

        return notificationDTO;
    }

    @Transactional("restaurantTransactionManager")
    @Override
    public OrderNotificationDTO updateOrderStatus(Long orderId, UpdateOrderStatusRequestDTO requestDTO) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomExceptionHandler("Order not found with id: " + orderId));

        order.setStatus(OrderStatus.valueOf(requestDTO.getStatus()));
        Order updatedOrder = orderRepository.save(order);

        OrderNotificationDTO notificationDTO = OrderNotificationDTO.fromOrder(updatedOrder);
        try {
            String orderJson = objectMapper.writeValueAsString(notificationDTO);
            kafkaProducerService.sendMessage("update-order-topic", orderJson);
        } catch (Exception e) {
            LOGGER.error("Failed to serialize order update notification or send to Kafka", e);
        }

        return notificationDTO;
    }

    @Transactional("restaurantTransactionManager")
    @Override
    public OrderNotificationDTO updateOrderStatusReadyToServe(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomExceptionHandler("Order not found with id: " + orderId));

        order.setStatus(OrderStatus.valueOf("READY_TO_SERVE"));
        Order updatedOrder = orderRepository.save(order);

        OrderNotificationDTO notificationDTO = OrderNotificationDTO.fromOrder(updatedOrder);
        try {
            String orderJson = objectMapper.writeValueAsString(notificationDTO);
            kafkaProducerService.sendMessage("serve-order-topic", orderJson);
        } catch (Exception err) {
            LOGGER.error("Failed to serialize order update notification or send to Kafka", err);
        }

        return notificationDTO;
    }

    @Transactional("restaurantTransactionManager")
    @Override
    public OrderNotificationDTO serveOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomExceptionHandler("Order not found with id: " + orderId));
        order.setStatus(OrderStatus.valueOf("SERVED"));
        Order savedOrder = orderRepository.save(order);
        return OrderNotificationDTO.fromOrder(savedOrder);
    }
}