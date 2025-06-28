package com.tlfdt.bonrecreme.controller.api.v1.cashier.dto.bill;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class BillRequestDTO {
    @NotNull(message = "Table id cannot be null.")
    @Positive(message = "Table id must be a positive number.")
    @JsonProperty("table_id")
    Long tableId;
}