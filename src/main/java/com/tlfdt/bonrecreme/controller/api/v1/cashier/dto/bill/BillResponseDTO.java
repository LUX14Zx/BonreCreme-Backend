package com.tlfdt.bonrecreme.controller.api.v1.cashier.dto.bill;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class BillResponseDTO {
    private Long billId;
    private Integer tableNumber;
    private BigDecimal totalAmount;
    private LocalDateTime billTime;
    private boolean isPaid;
    private List<OrderDTO> orders;

    @Data
    @Builder
    public static class OrderDTO {
        private Long orderId;
        private BigDecimal totalPrice;
        private List<OrderItemDTO> items;
    }

    @Data
    @Builder
    public static class OrderItemDTO {
        private String name;
        private int quantity;
        private BigDecimal price;
    }
}