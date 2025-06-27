package com.tlfdt.bonrecreme.service.bill.image;

import com.tlfdt.bonrecreme.controller.api.v1.cashier.dto.bill.BillResponseDTO;
import com.tlfdt.bonrecreme.service.bill.BillService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

@Service
@RequiredArgsConstructor
public class BillImageServiceImpl implements BillImageService {

    private final BillService billService;

    @Override
    public byte[] generateBillImage(Long tableId) {
        BillResponseDTO bill = billService.getBillForTable(tableId);
        String html = generateBillHtml(bill);
        return convertHtmlToImage(html);
    }

    private String generateBillHtml(BillResponseDTO bill) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body>");
        sb.append("<h1>Bill</h1>");
        sb.append("<p>Bill ID: ").append(bill.getBillId()).append("</p>");
        sb.append("<p>Table Number: ").append(bill.getTableNumber()).append("</p>");
        sb.append("<p>Total Amount: ").append(bill.getTotalAmount()).append("</p>");
        sb.append("<p>Bill Time: ").append(bill.getBillTime()).append("</p>");

        sb.append("<h2>Orders</h2>");
        for (BillResponseDTO.OrderDTO order : bill.getOrders()) {
            sb.append("<h3>Order ID: ").append(order.getOrderId()).append("</h3>");
            sb.append("<p>Total Price: ").append(order.getTotalPrice()).append("</p>");
            sb.append("<h4>Items</h4>");
            sb.append("<table border='1'><tr><th>Name</th><th>Quantity</th><th>Price</th></tr>");
            for (BillResponseDTO.OrderItemDTO item : order.getItems()) {
                sb.append("<tr>");
                sb.append("<td>").append(item.getName()).append("</td>");
                sb.append("<td>").append(item.getQuantity()).append("</td>");
                sb.append("<td>").append(item.getPrice()).append("</td>");
                sb.append("</tr>");
            }
            sb.append("</table>");
        }

        sb.append("</body></html>");
        return sb.toString();
    }

    private byte[] convertHtmlToImage(String html) {
        try {
            JEditorPane editorPane = new JEditorPane();
            editorPane.setEditable(false);
            editorPane.setContentType("text/html");
            editorPane.setText(html);

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
        } catch (Exception e) {
            // Handle exception
            throw new RuntimeException("Error generating bill image", e);
        }
    }
}