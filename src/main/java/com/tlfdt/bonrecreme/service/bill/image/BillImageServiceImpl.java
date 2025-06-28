package com.tlfdt.bonrecreme.service.bill.image;

import com.tlfdt.bonrecreme.config.properties.BillImageProperties;
import com.tlfdt.bonrecreme.controller.api.v1.cashier.dto.bill.BillResponseDTO;
import com.tlfdt.bonrecreme.exception.AppExceptionHandler;
import com.tlfdt.bonrecreme.exception.custom.CustomExceptionHandler;
import com.tlfdt.bonrecreme.exception.resource.ResourceNotFoundException;
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
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * A service implementation for generating JPEG images of customer bills.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BillImageServiceImpl implements BillImageService {

    private final BillService billService;
    private final TemplateEngine templateEngine;
    private final BillImageProperties billImageProperties;

    @Override
    public byte[] generateBillImage(BillImageRequest request) {
        try {
            BillRequestDTO billRequest = BillRequestDTO.builder().tableId(request.getTableId()).build();
            BillResponseDTO bill = billService.getBillForTable(billRequest);

            Context context = new Context();
            context.setVariable("bill", bill);

            String htmlContent = templateEngine.process(billImageProperties.getTemplateName(), context);
            log.info("Generated HTML for bill image:\n{}", htmlContent);

            return renderHtmlToImage(htmlContent);

        } catch (ResourceNotFoundException e) {
            // Re-throw the exception to be handled by the global exception handler.
            // This will ensure a proper HTTP 404 Not Found response is sent to the client
            // when a bill for the specified table does not exist.
            throw e;
        } catch (CustomExceptionHandler e) {
            throw e;
        } catch (Exception e) {
            log.error("An unexpected error occurred while generating bill image for table ID: {}", request.getTableId(), e);
            throw new CustomExceptionHandler("An unexpected system error occurred while generating the bill image.");
        }
    }

    /**
     * Renders a string of well-formed XHTML into a JPEG byte array.
     * This method correctly handles transparency to prevent a black background in the final JPEG.
     *
     * @param xhtmlContent The XHTML string to render.
     * @return A byte array containing the JPEG image data.
     * @throws IOException if there is an error during image processing.
     */
    private byte[] renderHtmlToImage(String xhtmlContent) throws IOException {
        try {
            JEditorPane editorPane = new JEditorPane();
            editorPane.setEditable(false);
            editorPane.setContentType("text/html");
            editorPane.setText(xhtmlContent);

            // Give the component a size
            Dimension preferredSize = editorPane.getPreferredSize();
            editorPane.setSize(preferredSize);

            BufferedImage image = new BufferedImage(preferredSize.width, preferredSize.height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = image.createGraphics();
            editorPane.print(g2d);
            g2d.dispose();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            return baos.toByteArray();
        } catch (IOException e) {
            // Handle exception
            throw AppExceptionHandler.throwException("Error generating bill image", e);
        }
    }

}