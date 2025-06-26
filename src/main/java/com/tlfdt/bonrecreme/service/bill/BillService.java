package com.tlfdt.bonrecreme.service.bill;

import com.tlfdt.bonrecreme.controller.api.v1.cashier.dto.bill.BillResponseDTO;

public interface BillService {
    BillResponseDTO checkoutBillTable(Long tableId);
}