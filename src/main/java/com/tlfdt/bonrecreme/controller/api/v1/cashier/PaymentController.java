package com.tlfdt.bonrecreme.controller.api.v1.cashier;

import com.tlfdt.bonrecreme.controller.api.v1.cashier.dto.bill.BillResponseDTO;
import com.tlfdt.bonrecreme.controller.api.v1.cashier.dto.payment.PaymentRequest;
import com.tlfdt.bonrecreme.controller.api.v1.dto.ApiResponseDTO;
import com.tlfdt.bonrecreme.service.bill.BillService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for processing bill payments.
 */
@RestController
@RequestMapping("/api/v1/cashier")
@RequiredArgsConstructor
@Validated // Enables method-level validation for path variables and request parameters.
public class PaymentController {

    private final BillService billService;

    /**
     * Processes the payment for a given bill ID.
     * This endpoint marks the specified bill as PAID.
     *
     * @param billId The unique identifier of the bill to be paid. Must be a positive number.
     * @return An ApiResponseDTO containing the details of the paid bill.
     */
    @PostMapping("/pay/{billId}")
    public ResponseEntity<ApiResponseDTO<BillResponseDTO>> payBill(
            @PathVariable @Positive(message = "Bill ID must be a positive number.") Long billId) {

        // Create a PaymentRequest object to pass to the service layer.
        PaymentRequest paymentRequest = PaymentRequest.builder().billId(billId).build();
        BillResponseDTO paidBill = billService.processPayment(paymentRequest);

        ApiResponseDTO<BillResponseDTO> response = ApiResponseDTO.<BillResponseDTO>builder()
                .data(paidBill)
                .status("success")
                .message("Payment successful. Bill marked as PAID.")
                .build();

        return ResponseEntity.ok(response);
    }
}
