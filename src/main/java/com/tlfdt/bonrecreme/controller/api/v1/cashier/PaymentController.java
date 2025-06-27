package com.tlfdt.bonrecreme.controller.api.v1.cashier;

import com.tlfdt.bonrecreme.controller.api.v1.cashier.dto.bill.BillResponseDTO;
import com.tlfdt.bonrecreme.controller.api.v1.dto.ApiResponseDTO;
import com.tlfdt.bonrecreme.service.bill.BillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cashier")
@RequiredArgsConstructor
public class PaymentController {

    private final BillService billService;

    @PostMapping("/bill/{billId}/pay")
    public ResponseEntity<ApiResponseDTO<BillResponseDTO>> payBill(@PathVariable Long billId) {
        BillResponseDTO paidBill = billService.processPayment(billId);

        ApiResponseDTO<BillResponseDTO> response = ApiResponseDTO.<BillResponseDTO>builder()
                .api_data(paidBill)
                .status("success")
                .message("Payment successful. Bill marked as PAID.")
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}