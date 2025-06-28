package com.tlfdt.bonrecreme.service.order;

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
import com.tlfdt.bonrecreme.config.message.kafka.producer.KafkaProducerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import org.mockito.ArgumentCaptor;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private MenuItemRepository menuItemRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private SeatTableRepository seatTableRepository;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @InjectMocks
    private OrderServiceImpl orderService;

    private SeatTable seatTable;
    private MenuItem menuItem;
    private Order order;
    private MenuRequestDTO menuRequestDTO;
    private UpdateOrderRequestDTO updateOrderRequestDTO;
    private UpdateOrderStatusRequestDTO updateOrderStatusRequestDTO;

    @BeforeEach
    void setUp() {
        seatTable = SeatTable.builder()
                .id(1L)
                .tableNumber(101)
                .build();

        menuItem = MenuItem.builder()
                .id(1L)
                .name("Test Item")
                .price(BigDecimal.TEN)
                .build();

        order = Order.builder()
                .id(1L)
                .seatTable(seatTable)
                .status(OrderStatus.PENDING)
                .orderItems(new java.util.HashSet<>())
                .build();

        menuRequestDTO = new MenuRequestDTO(1L, Collections.singletonList(new MenuRequestDTO.OrderItemRequest(1L, 2, null)));

        updateOrderRequestDTO = new UpdateOrderRequestDTO(Collections.singletonList(new MenuRequestDTO.OrderItemRequest(1L, 3, null)));

        updateOrderStatusRequestDTO = new UpdateOrderStatusRequestDTO(OrderStatus.COOKING);
    }

    @Test
    void testCreateOrder_Success() {
        when(seatTableRepository.findById(anyLong())).thenReturn(Optional.of(seatTable));
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        when(orderRepository.save(orderCaptor.capture())).thenReturn(Order.builder().id(1L).seatTable(seatTable).status(OrderStatus.PENDING).orderItems(new java.util.HashSet<>()).build());
        when(menuItemRepository.findById(anyLong())).thenReturn(Optional.of(menuItem));
        doNothing().when(kafkaProducerService).sendMessage(anyString(), any(OrderNotificationDTO.class));

        OrderNotificationDTO result = orderService.createOrder(menuRequestDTO);

        assertNotNull(result);
        assertEquals(orderCaptor.getValue().getId(), result.getOrderId());
        verify(seatTableRepository, times(1)).findById(anyLong());
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(menuItemRepository, times(1)).findById(anyLong());
        // KafkaProducerService is mocked but not explicitly verified as per instructions
    }

    @Test
    void testCreateOrder_TableNotFound() {
        when(seatTableRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> orderService.createOrder(menuRequestDTO));

        assertEquals("SeatTable not found with id: 1", exception.getMessage());
        verify(seatTableRepository, times(1)).findById(anyLong());
        verifyNoInteractions(orderRepository, menuItemRepository, kafkaProducerService);
    }

    @Test
    void testCreateOrder_MenuItemNotFound() {
        when(seatTableRepository.findById(anyLong())).thenReturn(Optional.of(seatTable));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(menuItemRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> orderService.createOrder(menuRequestDTO));

        assertEquals("menuitems not found with id: 1", exception.getMessage());
        verify(seatTableRepository, times(1)).findById(anyLong());
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(menuItemRepository, times(1)).findById(anyLong());
        verifyNoInteractions(kafkaProducerService);
    }

    @Test
    void testUpdateOrderItems_Success() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
        when(menuItemRepository.findById(anyLong())).thenReturn(Optional.of(menuItem));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderNotificationDTO result = orderService.updateOrderItems(1L, updateOrderRequestDTO);

        assertNotNull(result);
        assertEquals(order.getId(), result.getOrderId());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        verify(orderRepository, times(1)).findById(anyLong());
        verify(menuItemRepository, times(1)).findById(anyLong());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void testUpdateOrderItems_OrderNotFound() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> orderService.updateOrderItems(1L, updateOrderRequestDTO));

        assertEquals("Order not found with id: 1", exception.getMessage());
        verify(orderRepository, times(1)).findById(anyLong());
        verifyNoInteractions(menuItemRepository, kafkaProducerService);
    }

    @Test
    void testUpdateOrderStatus_Success() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        doNothing().when(kafkaProducerService).sendMessage(anyString(), any(OrderNotificationDTO.class));

        OrderNotificationDTO result = orderService.updateOrderStatus(1L, updateOrderStatusRequestDTO);

        assertNotNull(result);
        assertEquals(order.getId(), result.getOrderId());
        assertEquals(OrderStatus.COOKING, order.getStatus());
        verify(orderRepository, times(1)).findById(anyLong());
        verify(orderRepository, times(1)).save(any(Order.class));
        // KafkaProducerService is mocked but not explicitly verified as per instructions
    }

    @Test
    void testUpdateOrderStatus_OrderNotFound() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> orderService.updateOrderStatus(1L, updateOrderStatusRequestDTO));

        assertEquals("Order not found with id: 1", exception.getMessage());
        verify(orderRepository, times(1)).findById(anyLong());
        verifyNoInteractions(kafkaProducerService);
    }

    @Test
    void testMarkOrderAsReadyToServe_Success() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        doNothing().when(kafkaProducerService).sendMessage(anyString(), any(OrderNotificationDTO.class));

        OrderNotificationDTO result = orderService.markOrderAsReadyToServe(1L);

        assertNotNull(result);
        assertEquals(order.getId(), result.getOrderId());
        assertEquals(OrderStatus.READY_TO_SERVE, order.getStatus());
        verify(orderRepository, times(1)).findById(anyLong());
        verify(orderRepository, times(1)).save(any(Order.class));
        // KafkaProducerService is mocked but not explicitly verified as per instructions
    }

    @Test
    void testMarkOrderAsReadyToServe_OrderNotFound() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> orderService.markOrderAsReadyToServe(1L));

        assertEquals("Order not found with id: 1", exception.getMessage());
        verify(orderRepository, times(1)).findById(anyLong());
        verifyNoInteractions(kafkaProducerService);
    }

    @Test
    void testMarkOrderAsServed_Success() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        doNothing().when(kafkaProducerService).sendMessage(anyString(), any(OrderNotificationDTO.class));

        OrderNotificationDTO result = orderService.markOrderAsServed(1L);

        assertNotNull(result);
        assertEquals(order.getId(), result.getOrderId());
        assertEquals(OrderStatus.SERVED, order.getStatus());
        verify(orderRepository, times(1)).findById(anyLong());
        verify(orderRepository, times(1)).save(any(Order.class));
        // KafkaProducerService is mocked but not explicitly verified as per instructions
    }

    @Test
    void testMarkOrderAsServed_OrderNotFound() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> orderService.markOrderAsServed(1L));

        assertEquals("Order not found with id: 1", exception.getMessage());
        verify(orderRepository, times(1)).findById(anyLong());
        verifyNoInteractions(kafkaProducerService);
    }
}
