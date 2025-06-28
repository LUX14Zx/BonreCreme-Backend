package com.tlfdt.bonrecreme.service.bill.image;

import com.tlfdt.bonrecreme.config.properties.BillImageProperties;
import com.tlfdt.bonrecreme.controller.api.v1.cashier.dto.bill.BillResponseDTO;
import com.tlfdt.bonrecreme.exception.custom.CustomExceptionHandler;
import com.tlfdt.bonrecreme.service.bill.BillService;
import com.tlfdt.bonrecreme.controller.api.v1.cashier.dto.bill.BillRequestDTO;
import com.tlfdt.bonrecreme.controller.api.v1.cashier.dto.bill.image.BillImageRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.swing.Java2DRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * A service implementation for generating PNG images of customer bills.
 * <p>
 * This implementation uses the Thymeleaf template engine to process an HTML template,
 * which is then rendered into an image using the Flying Saucer (XHTMLRenderer) library.
 * This approach is server-safe, configurable, and separates presentation from logic.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BillImageServiceImpl implements BillImageService {

    private final BillService billService;
    private final TemplateEngine templateEngine;
    private final BillImageProperties billImageProperties; // Inject configuration

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] generateBillImage(BillImageRequest request) {
        try {
            // 1. Fetch the bill data using the DTO-based service method
            BillRequestDTO billRequest = BillRequestDTO.builder().tableId(request.getTableId()).build();
            BillResponseDTO bill = billService.getBillForTable(billRequest);

            // 2. Prepare the data model for the template
            Context context = new Context();
            context.setVariable("bill", bill);

            // 3. Process the HTML template with the data model
            String htmlContent = templateEngine.process(billImageProperties.getTemplateName(), context);

            // 4. Render the processed HTML to an image
            return renderHtmlToPng(htmlContent);

        } catch (IOException e) {
            log.error("Failed to generate bill image for table ID: {}", request.getTableId(), e);
            throw new CustomExceptionHandler("Error generating bill image. Please try again.");
        } catch (CustomExceptionHandler e) {
            // Re-throw known business exceptions to be handled by the controller advice
            throw e;
        } catch (Exception e) {
            log.error("An unexpected error occurred while generating bill image for table ID: {}", request.getTableId(), e);
            throw new CustomExceptionHandler("An unexpected system error occurred.");
        }
    }

    /**
     * Renders a string of well-formed XHTML into a PNG byte array.
     *
     * @param xhtmlContent The XHTML string to render.
     * @return A byte array containing the PNG image data.
     * @throws IOException if there is an error during image processing.
     */
    private byte[] renderHtmlToPng(String xhtmlContent) throws IOException {
        // Use the configured width from application.yml
        int imageWidth = billImageProperties.getWidth();
        Java2DRenderer renderer = new Java2DRenderer(xhtmlContent, imageWidth);
        BufferedImage image = renderer.getImage();

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", baos);
            return baos.toByteArray();
        }
    }
}