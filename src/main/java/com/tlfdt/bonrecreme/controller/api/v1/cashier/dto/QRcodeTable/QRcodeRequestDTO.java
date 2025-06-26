package com.tlfdt.bonrecreme.controller.api.v1.cashier.dto.QRcodeTable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for generating QR code for a restaurant table
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QRcodeRequestDTO {

    private Long tableId;
}
