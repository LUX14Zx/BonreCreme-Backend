package com.tlfdt.bonrecreme.controller.api.v1.cashier.dto.checkout;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CheckoutRequest {
    @NotNull(message = "table id cannot be null.")
    @Positive(message = "table id must be a positive number.")
    @JsonProperty("table_id")
    Long tableId;
}