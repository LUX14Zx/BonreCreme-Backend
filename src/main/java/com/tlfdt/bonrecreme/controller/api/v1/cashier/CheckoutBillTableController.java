package com.tlfdt.bonrecreme.controller.api.v1.cashier;

import com.tlfdt.bonrecreme.controller.api.v1.cashier.dto.bill.BillResponseDTO;
import com.tlfdt.bonrecreme.controller.api.v1.cashier.dto.bill.image.BillImageRequest;
import com.tlfdt.bonrecreme.controller.api.v1.cashier.dto.checkout.CheckoutRequest;
import com.tlfdt.bonrecreme.controller.api.v1.dto.ApiResponseDTO;
import com.tlfdt.bonrecreme.service.bill.BillService;
import com.tlfdt.bonrecreme.service.bill.image.BillImageService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for handling cashier operations related to checking out bills.
 * This includes generating a bill image and finalizing the checkout process for a table.
 */
@RestController
@RequestMapping("/api/v1/cashier")
@RequiredArgsConstructor
@Validated // Enables method-level validation for @Positive, @NotBlank, etc.
public class CheckoutBillTableController {

    private final BillService billService;
    private final BillImageService billImageService;

    /**
     * Generates and returns a PNG image of the current bill for a given table.
     *
     * @param tableId The unique identifier of the table. Must be a positive number.
     * @return A ResponseEntity containing the bill image as a byte array.
     */
    @GetMapping(
            path = "/bill/image/{tableId}",
            produces = MediaType.IMAGE_PNG_VALUE
    )
    public ResponseEntity<byte[]> getBillImage(
            @PathVariable @Positive(message = "Table ID must be a positive number.") Long tableId) {
        // Create a BillImageRequest object
        BillImageRequest request = BillImageRequest.builder().tableId(tableId).build();
        byte[] image = billImageService.generateBillImage(request);
        return ResponseEntity.ok(image);
    }

    /**
     * Finalizes the bill for a specific table, marking it for payment.
     *
     * @param tableId The unique identifier of the table to check out. Must be a positive number.
     * @return An ApiResponseDTO containing the details of the generated bill.
     */
    @PostMapping("/checkout-bill/{tableId}")
    public ResponseEntity<ApiResponseDTO<BillResponseDTO>> checkoutBillForTable(
            @PathVariable @Positive(message = "Table ID must be a positive number.") Long tableId) {
        // Create a CheckoutRequest object
        CheckoutRequest request = CheckoutRequest.builder().tableId(tableId).build();
        BillResponseDTO billTable = billService.checkoutBillTable(request);
        ApiResponseDTO<BillResponseDTO> response = ApiResponseDTO.<BillResponseDTO>builder()
                .data(billTable)
                .status("success")
                .message("Bill checked out successfully")
                .build();

        return ResponseEntity.ok(response);
    }
}