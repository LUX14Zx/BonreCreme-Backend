package com.tlfdt.bonrecreme.api.v1.cashier;

import com.tlfdt.bonrecreme.api.v1.cashier.dto.QRcodeTable.QRcodeRequestDTO;
import com.tlfdt.bonrecreme.api.v1.cashier.dto.QRcodeTable.QRcodeResponseDTO;
import com.tlfdt.bonrecreme.repository.restaurant.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for QR code generation for restaurant tables
 */
@RestController
@RequestMapping("/api/v1/cashier/qrcode")
@RequiredArgsConstructor
public class QRcodeTable {

    private final RestaurantRepository restaurantRepository;

    /**
     * Generate a QR code for a specific table
     * @param request The QR code generation request
     * @return QR code response with encoded data and metadata
     */
    /*
    @PostMapping("/tableid")
    public ResponseEntity<QRcodeResponseDTO> generateQRCode(@Validated @RequestBody QRcodeRequestDTO request) {
        // Validate request
        if (request.getTableId() == null) {
            return ResponseEntity.badRequest().body(
                    new QRcodeResponseDTO(null,
                            null,
                            null,
                            "Can't reserved this table")
            );
        }

        // In a real implementation, you would:
        // 1. Fetch the table and table status from database
        // 2. Fetch the restaurant from database
        // 3. Generate actual QR code data (could be a URL, JSON, etc.)
        // 4. Possibly store the QR code or its metadata
        // 5. Return the response with all relevant data


    }*/
}
