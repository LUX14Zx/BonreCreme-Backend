package com.tlfdt.bonrecreme.controller.api.v1.cashier.dto.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class PaymentRequest {
    @NotNull(message = "Bill ID cannot be null.")
    @Positive(message = "Bill ID must be a positive number.")
    @JsonProperty("bill_id")
    Long billId;
    // Example of future extension:
    // String paymentMethod;
    // BigDecimal amountTendered;
}