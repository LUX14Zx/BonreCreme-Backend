package com.tlfdt.bonrecreme.controller.api.v1.cashier;

import com.tlfdt.bonrecreme.controller.api.v1.cashier.dto.bill.BillResponseDTO;
import com.tlfdt.bonrecreme.controller.api.v1.dto.ApiResponseDTO;
import com.tlfdt.bonrecreme.service.bill.BillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cashier/")
@RequiredArgsConstructor
public class CheckoutBillTableController {

    private final BillService billService;

    @PostMapping(
            path = "/checkout-bill/{tableId}"
    )
    public ResponseEntity<ApiResponseDTO<BillResponseDTO>> CheckoutBill(@PathVariable Long tableId)
    {
        BillResponseDTO billTable = billService.checkoutBillTable(tableId);

        ApiResponseDTO<BillResponseDTO> response = ApiResponseDTO.<BillResponseDTO>builder()
                .api_data(billTable)
                .status("success")
                .message("Bill checked out successfully")
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}