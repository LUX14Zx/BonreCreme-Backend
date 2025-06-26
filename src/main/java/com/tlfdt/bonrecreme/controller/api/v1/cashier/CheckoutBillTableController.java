package com.tlfdt.bonrecreme.controller.api.v1.cashier;

import com.tlfdt.bonrecreme.controller.api.v1.dto.ApiResponseDTO;
import com.tlfdt.bonrecreme.model.restaurant.Bill;
import com.tlfdt.bonrecreme.model.restaurant.Order;
import com.tlfdt.bonrecreme.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/cashier/")
@RequiredArgsConstructor
public class CheckoutBillTableController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckoutBillTableController.class);
    private final OrderService orderService;

    @PostMapping(
            path = "/checkout-bill/{tableId}"
    )
    public ResponseEntity<ApiResponseDTO<Bill>> CheckoutBill(@PathVariable Long tableId)
    {

        Bill billTable = orderService.checkoutBillTable(tableId);

        ApiResponseDTO<Bill> response = ApiResponseDTO.<Bill>builder()
                .api_data(billTable)
                .status("success")
                .message("Order updated successfully")
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
