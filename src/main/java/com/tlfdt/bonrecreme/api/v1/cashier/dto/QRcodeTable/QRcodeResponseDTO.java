package com.tlfdt.bonrecreme.api.v1.cashier.dto.QRcodeTable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for QR code generation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QRcodeResponseDTO {

    private String qrCodeUrl;

    private Long tableId;

    private Integer tableNumber;

    private String response_info;
}
