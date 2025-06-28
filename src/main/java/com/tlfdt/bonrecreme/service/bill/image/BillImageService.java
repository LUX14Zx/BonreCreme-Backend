package com.tlfdt.bonrecreme.service.bill.image;

import com.tlfdt.bonrecreme.exception.custom.CustomExceptionHandler;
import com.tlfdt.bonrecreme.controller.api.v1.cashier.dto.bill.image.BillImageRequest;

/**
 * A service interface for generating image representations of customer bills.
 * <p>
 * This service abstracts the process of converting bill data into a visual format,
 * such as a PNG image, which can then be displayed or printed.
 */
public interface BillImageService {

    /**
     * Generates a bill image based on the provided request parameters.
     * <p>
     * This method retrieves the current bill data for the specified table and renders it
     * into a byte array representing a PNG image.
     *
     * @param request An object containing all necessary parameters for generating the image,
     * such as the table ID.
     * @return A byte array containing the generated PNG image data.
     * @throws CustomExceptionHandler if no bill is found for the specified table or if
     * an error occurs during image generation.
     */
    byte[] generateBillImage(BillImageRequest request);
}