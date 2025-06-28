package com.tlfdt.bonrecreme.service.bill;

import com.tlfdt.bonrecreme.controller.api.v1.cashier.dto.bill.BillResponseDTO;
import com.tlfdt.bonrecreme.exception.custom.CustomExceptionHandler;
import com.tlfdt.bonrecreme.model.restaurant.Bill;
import com.tlfdt.bonrecreme.model.restaurant.Order;
import com.tlfdt.bonrecreme.model.restaurant.enums.BillStatus;
import com.tlfdt.bonrecreme.model.restaurant.enums.OrderStatus;
import com.tlfdt.bonrecreme.repository.restaurant.BillRepository;
import com.tlfdt.bonrecreme.repository.restaurant.OrderRepository;
import com.tlfdt.bonrecreme.controller.api.v1.cashier.dto.bill.*;
import com.tlfdt.bonrecreme.controller.api.v1.cashier.dto.checkout.*;
import com.tlfdt.bonrecreme.controller.api.v1.cashier.dto.payment.*;
import com.tlfdt.bonrecreme.utils.bill.BillFactory;
import com.tlfdt.bonrecreme.utils.bill.mapper.BillMapper;
import com.tlfdt.bonrecreme.service.bill.messaging.BillEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BillServiceImpl implements BillService {

    private final BillRepository billRepository;
    private final OrderRepository orderRepository;
    private final BillFactory billFactory;
    private final BillMapper billMapper;
    private final BillEventPublisher billEventPublisher;

    @Override
    @Transactional("restaurantTransactionManager")
    public BillResponseDTO checkoutBillTable(CheckoutRequest request) {
        // 1. Fetch all data needed in one single, optimized query to prevent N+1
        List<Order> servedOrders = orderRepository.findUnbilledOrdersForTableWithDetails(
                request.getTableId(),
                OrderStatus.SERVED
        );

        if (servedOrders.isEmpty()) {
            throw new CustomExceptionHandler("No served orders found for table with id: " + request.getTableId());
        }

        // 2. Perform business logic calculations
        BigDecimal totalAmount = calculateTotalAmount(servedOrders);
        if (totalAmount.compareTo(BigDecimal.ZERO) == 0) {
            log.warn("Checkout attempted for table {} with zero total amount.", request.getTableId());
            // Depending on business rules, you might throw an exception here
        }

        // 3. Use the factory to create the entity object, converting the List to a Set
        Bill bill = billFactory.createPendingBill(servedOrders.getFirst().getSeatTable(), new HashSet<>(servedOrders), totalAmount);

        // 4. Update the state of related entities
        for (Order order : servedOrders) {
            order.setBill(bill);
            order.setStatus(OrderStatus.BILLED);
        }

        Bill savedBill = billRepository.save(bill);
        log.info("Checked out bill {} for table {}", savedBill.getId(), request.getTableId());

        // 5. Use the mapper to create the response DTO
        return billMapper.toBillResponseDTO(savedBill);
    }

    @Override
    @Transactional("restaurantTransactionManager")
    public BillResponseDTO processPayment(PaymentRequest request) {
        // The findById is sufficient here, but a custom query could fetch orders if needed
        Bill bill = billRepository.findById(request.getBillId())
                .orElseThrow(() -> new CustomExceptionHandler("Bill not found with id: " + request.getBillId()));

        if (bill.getStatus() == BillStatus.PAID) {
            throw new CustomExceptionHandler("Bill with id: " + request.getBillId() + " has already been paid.");
        }

        bill.setStatus(BillStatus.PAID);
        bill.getOrders().forEach(order -> order.setStatus(OrderStatus.PAID));

        Bill paidBill = billRepository.save(bill);
        log.info("Processed payment for bill {}", paidBill.getId());

        BillResponseDTO billResponseDTO = billMapper.toBillResponseDTO(paidBill);

        // Publish an event that the bill has been paid
        billEventPublisher.publishBillPaidEvent(billResponseDTO);

        return billResponseDTO;
    }

    @Override
    @Transactional(value = "restaurantTransactionManager", readOnly = true)
    public BillResponseDTO getBillForTable(BillRequestDTO request) {
        Bill bill = billRepository.findMostRecentBillWithDetailsByTableIdAndStatus(request.getTableId(), BillStatus.PENDING)
                .orElseThrow(() -> new CustomExceptionHandler("No pending bill found for table with id: " + request.getTableId()));

        return billMapper.toBillResponseDTO(bill);
    }

    /**
     * Calculates the total amount from a list of orders based on the price at the time of the order.
     */
    private BigDecimal calculateTotalAmount(List<Order> orders) {
        return orders.stream()
                .flatMap(order -> order.getOrderItems().stream())
                // Use getPriceAtTime() for accurate historical pricing
                .map(item -> item.getPriceAtTime().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}