package com.tlfdt.bonrecreme.service.bill;

import com.tlfdt.bonrecreme.controller.api.v1.cashier.dto.bill.BillResponseDTO;
import com.tlfdt.bonrecreme.model.restaurant.Bill;
import org.springframework.transaction.annotation.Transactional;

public interface BillService {

    Bill generateBillForTable(Long tableId);
}