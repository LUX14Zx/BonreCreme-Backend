package com.tlfdt.bonrecreme.service.bill;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tlfdt.bonrecreme.controller.api.v1.cashier.dto.bill.BillResponseDTO;
import com.tlfdt.bonrecreme.exception.custom.CustomExceptionHandler;
import com.tlfdt.bonrecreme.model.restaurant.*;
import com.tlfdt.bonrecreme.model.restaurant.enums.BillStatus;
import com.tlfdt.bonrecreme.model.restaurant.enums.OrderStatus;
import com.tlfdt.bonrecreme.repository.restaurant.BillRepository;
import com.tlfdt.bonrecreme.repository.restaurant.MenuItemRepository;
import com.tlfdt.bonrecreme.repository.restaurant.OrderRepository;
import com.tlfdt.bonrecreme.repository.restaurant.SeatTableRepository;
import com.tlfdt.bonrecreme.service.order.masseging.KafkaProducerService;
import com.tlfdt.bonrecreme.utils.bill.GetBill;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BillServiceImpl implements BillService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BillServiceImpl.class);


    private final BillRepository billRepository;
    private final OrderRepository orderRepository;
    private final MenuItemRepository menuItemRepository;
    private final SeatTableRepository seatTableRepository;
    private final KafkaProducerService kafkaProducerService;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional("restaurantTransactionManager")
    public BillResponseDTO checkoutBillTable(Long tableId) {
        List<Order> servedOrders = orderRepository.findBySeatTableIdAndStatusAndBillIsNull(tableId, OrderStatus.SERVED);

        if (servedOrders.isEmpty()) {
            throw new CustomExceptionHandler("No served orders found for table with id: " + tableId);
        }

        SeatTable seatTable = seatTableRepository.findById(tableId)
                .orElseThrow(() -> new CustomExceptionHandler("SeatTable not found with id: " + tableId));

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (Order orderToBill : servedOrders) {
            if (orderToBill.getOrderItems() != null) {
                for (OrderItem item : orderToBill.getOrderItems()) {
                    MenuItem menuItem = menuItemRepository.findById(item.getMenuItem().getId())
                            .orElseThrow(() -> new CustomExceptionHandler("MenuItem not found for order item: " + item.getId()));
                    totalAmount = totalAmount.add(menuItem.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                }
            }
        }
        Bill bill = GetBill.getBill(seatTable, servedOrders, totalAmount);
        Bill savedBill = billRepository.save(bill);

        for (Order order : servedOrders) {
            order.setBill(savedBill);
            order.setStatus(OrderStatus.BILLED);
            orderRepository.save(order);
        }

        return toBillResponseDTO(savedBill);
    }

    private BillResponseDTO toBillResponseDTO(Bill bill) {
        List<BillResponseDTO.OrderDTO> orderDTOs = bill.getOrders().stream()
                .map(this::toOrderDTO)
                .collect(Collectors.toList());

        return BillResponseDTO.builder()
                .billId(bill.getId())
                .tableNumber(bill.getSeatTable().getTableNumber())
                .totalAmount(bill.getTotalAmount())
                .billTime(bill.getCreatedAt())
                .isPaid(bill.getStatus() == BillStatus.PAID)
                .orders(orderDTOs)
                .build();
    }

    private BillResponseDTO.OrderDTO toOrderDTO(Order order) {
        List<BillResponseDTO.OrderItemDTO> orderItemDTOs = order.getOrderItems().stream()
                .map(this::toOrderItemDTO)
                .collect(Collectors.toList());

        BigDecimal orderTotal = orderItemDTOs.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return BillResponseDTO.OrderDTO.builder()
                .orderId(order.getId())
                .totalPrice(orderTotal)
                .items(orderItemDTOs)
                .build();
    }

    private BillResponseDTO.OrderItemDTO toOrderItemDTO(OrderItem orderItem) {
        return BillResponseDTO.OrderItemDTO.builder()
                .name(orderItem.getMenuItem().getName())
                .quantity(orderItem.getQuantity())
                .price(orderItem.getMenuItem().getPrice())
                .build();
    }

    @Override
    @Transactional("restaurantTransactionManager")
    public BillResponseDTO processPayment(Long billId) {
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new CustomExceptionHandler("Bill not found with id: " + billId));

        bill.setStatus(BillStatus.PAID);

        for (Order order : bill.getOrders()) {
            order.setStatus(OrderStatus.PAID);
        }

        Bill paidBill = billRepository.save(bill);
        BillResponseDTO billResponseDTO = toBillResponseDTO(paidBill);

        try {
            String billMessage = objectMapper.writeValueAsString(billResponseDTO);
            kafkaProducerService.sendMessage("paid-bills-topic", billMessage);
        } catch (JsonProcessingException e) {
            LOGGER.error("Error serializing bill to JSON", e);
        }

        return billResponseDTO;
    }

    @Override
    @Transactional(value = "restaurantTransactionManager", readOnly = true)
    public BillResponseDTO getBillForTable(Long tableId) {
        Bill bill = billRepository.findFirstBySeatTable_IdAndStatusOrderByCreatedAtDesc(tableId, BillStatus.PENDING)
                .orElseThrow(() -> new CustomExceptionHandler("No pending bill found for table with id: " + tableId));
        return toBillResponseDTO(bill);
    }

}