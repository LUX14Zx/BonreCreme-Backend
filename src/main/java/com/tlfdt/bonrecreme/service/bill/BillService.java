package com.tlfdt.bonrecreme.service.bill;

import com.tlfdt.bonrecreme.controller.api.v1.cashier.dto.bill.BillResponseDTO;
import com.tlfdt.bonrecreme.exception.custom.CustomExceptionHandler;
import com.tlfdt.bonrecreme.controller.api.v1.cashier.dto.bill.*;
import com.tlfdt.bonrecreme.controller.api.v1.cashier.dto.checkout.*;
import com.tlfdt.bonrecreme.controller.api.v1.cashier.dto.payment.*;
import jakarta.validation.Valid;

/**
 * A service interface for managing the billing and payment lifecycle.
 * <p>
 * This service provides core functionalities for checking out a table's bill,
 * processing payments, and retrieving bill information.
 */
public interface BillService {

    /**
     * Checks out a table by generating a new bill for all its served, unbilled orders.
     *
     * @param request The request object containing the ID of the table to check out.
     * @return A {@link BillResponseDTO} representing the newly created bill.
     * @throws CustomExceptionHandler if the table is not found or has no served orders to bill.
     */
    BillResponseDTO checkoutBillTable(@Valid CheckoutRequest request);

    /**
     * Processes a payment for a specific bill, marking it as PAID.
     *
     * @param request The request object containing the ID of the bill to be paid.
     * @return A {@link BillResponseDTO} representing the updated, paid bill.
     * @throws CustomExceptionHandler if the bill is not found or is already paid.
     */
    BillResponseDTO processPayment(@Valid PaymentRequest request);

    /**
     * Retrieves the current pending bill for a specific table.
     *
     * @param request The request object containing the ID of the table.
     * @return A {@link BillResponseDTO} representing the pending bill.
     * @throws CustomExceptionHandler if the table or a pending bill for it is not found.
     */
    BillResponseDTO getBillForTable(@Valid BillRequestDTO request);
}