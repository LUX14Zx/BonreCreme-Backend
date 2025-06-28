package com.tlfdt.bonrecreme.controller.api.v1.cashier.dto.bill.image;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Value;

/**
 * Represents the request parameters for generating a bill image.
 * <p>
 * Using a dedicated request object instead of primitive types makes the
 * service method signature more stable and easier to extend. For example,
 * we could add parameters like image format (PNG, JPEG) or dimensions
 * in the future without breaking existing code.
 */
@Value
@Builder
public class BillImageRequest {

    /**
     * The unique identifier of the table for which to generate the bill image.
     */
    @NotNull(message = "table ID cannot be null.")
    @Positive(message = "table ID must be a positive number.")
    Long tableId;

}