package com.tlfdt.bonrecreme.service.report;

import com.tlfdt.bonrecreme.model.restaurant.Bill;
import com.tlfdt.bonrecreme.model.restaurant.enums.BillStatus;
import com.tlfdt.bonrecreme.repository.restaurant.BillRepository;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalesReportServiceImpl implements SalesReportService {

    private final BillRepository billRepository;

    @Override
    public byte[] generateSalesReportCSV(int year, int month) {
        List<Bill> paidBills = billRepository.findAll().stream()
                .filter(bill -> bill.getStatus() == BillStatus.PAID &&
                        bill.getCreatedAt().getYear() == year &&
                        bill.getCreatedAt().getMonthValue() == month)
                .toList();

        StringBuilder csvBuilder = new StringBuilder();
        // Header
        csvBuilder.append("Bill ID,Date,Total Amount\n");

        // Data
        BigDecimal totalRevenue = BigDecimal.ZERO;
        for (Bill bill : paidBills) {
            csvBuilder.append(bill.getId()).append(",");
            csvBuilder.append(bill.getCreatedAt().getDayOfMonth()).append(",");
            csvBuilder.append(bill.getTotalAmount()).append("\n");
            totalRevenue = totalRevenue.add(bill.getTotalAmount());
        }

        // Summary Footer
        csvBuilder.append("\n,,Total Revenue,").append(totalRevenue);

        return csvBuilder.toString().getBytes(StandardCharsets.UTF_8);
    }
    @Override
    public byte[] generateSalesReportPDF(int year, int month) throws JRException {
        List<Bill> paidBills = billRepository.findAll().stream()
                .filter(bill -> bill.getStatus() == BillStatus.PAID &&
                        bill.getCreatedAt().getYear() == year &&
                        bill.getCreatedAt().getMonthValue() == month)
                .toList();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        List<Map<String, Object>> billMaps = paidBills.stream().map(bill -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", bill.getId());
            map.put("createdAt", bill.getCreatedAt().format(formatter)); // Format as a String
            map.put("tableNumber", bill.getSeatTable().getTableNumber());
            map.put("totalAmount", bill.getTotalAmount());
            return map;
        }).collect(Collectors.toList());

        // Load and compile the JRXML template
        String jrxmlTemplate = createJrxmlTemplate();
        InputStream inputStream = new ByteArrayInputStream(jrxmlTemplate.getBytes(StandardCharsets.UTF_8));
        JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);

        // Create a data source from the list of maps
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(billMaps);

        // Add parameters
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("ReportTitle", "Monthly Sales Report");
        parameters.put("MonthYear", Month.of(month).name() + " " + year);

        // Fill the report
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        // Export to PDF
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }

    private String createJrxmlTemplate() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<jasperReport xmlns=\"http://jasperreports.sourceforge.net/jasperreports\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://jasperreports.sourceforge.net/jasperreports http://jasperreports.sourceforge.net/xsd/jasperreport.xsd\" name=\"SalesReport\" pageWidth=\"595\" pageHeight=\"842\" columnWidth=\"555\" leftMargin=\"20\" rightMargin=\"20\" topMargin=\"20\" bottomMargin=\"20\">"
                + "<parameter name=\"ReportTitle\" class=\"java.lang.String\"/>"
                + "<parameter name=\"MonthYear\" class=\"java.lang.String\"/>"
                + "<queryString><![CDATA[]]></queryString>"
                + "<field name=\"id\" class=\"java.lang.Long\"/>"
                + "<field name=\"createdAt\" class=\"java.lang.String\"/>" // Changed to String
                + "<field name=\"tableNumber\" class=\"java.lang.Integer\"/>"
                + "<field name=\"totalAmount\" class=\"java.math.BigDecimal\"/>"
                + "<title>"
                + "<band height=\"50\">"
                + "<textField><reportElement x=\"0\" y=\"10\" width=\"555\" height=\"30\"/>"
                + "<textElement textAlignment=\"Center\" verticalAlignment=\"Middle\"><font size=\"18\" isBold=\"true\"/></textElement>"
                + "<textFieldExpression><![CDATA[$P{ReportTitle}]]></textFieldExpression></textField>"
                + "<textField><reportElement x=\"0\" y=\"30\" width=\"555\" height=\"20\"/>"
                + "<textElement textAlignment=\"Center\" verticalAlignment=\"Middle\"><font size=\"12\"/></textElement>"
                + "<textFieldExpression><![CDATA[$P{MonthYear}]]></textFieldExpression></textField>"
                + "</band>"
                + "</title>"
                + "<columnHeader>"
                + "<band height=\"30\">"
                + "<staticText><reportElement mode=\"Opaque\" x=\"0\" y=\"0\" width=\"100\" height=\"30\" backcolor=\"#D3D3D3\"/><textElement textAlignment=\"Center\" verticalAlignment=\"Middle\"/>"
                + "<text><![CDATA[Bill ID]]></text></staticText>"
                + "<staticText><reportElement mode=\"Opaque\" x=\"100\" y=\"0\" width=\"200\" height=\"30\" backcolor=\"#D3D3D3\"/><textElement textAlignment=\"Center\" verticalAlignment=\"Middle\"/>"
                + "<text><![CDATA[Date]]></text></staticText>"
                + "<staticText><reportElement mode=\"Opaque\" x=\"300\" y=\"0\" width=\"155\" height=\"30\" backcolor=\"#D3D3D3\"/><textElement textAlignment=\"Center\" verticalAlignment=\"Middle\"/>"
                + "<text><![CDATA[Table Number]]></text></staticText>"
                + "<staticText><reportElement mode=\"Opaque\" x=\"455\" y=\"0\" width=\"100\" height=\"30\" backcolor=\"#D3D3D3\"/><textElement textAlignment=\"Center\" verticalAlignment=\"Middle\"/>"
                + "<text><![CDATA[Amount]]></text></staticText>"
                + "</band>"
                + "</columnHeader>"
                + "<detail>"
                + "<band height=\"20\">"
                + "<textField><reportElement x=\"0\" y=\"0\" width=\"100\" height=\"20\"/><textElement textAlignment=\"Center\" verticalAlignment=\"Middle\"/><textFieldExpression><![CDATA[$F{id}]]></textFieldExpression></textField>"
                + "<textField><reportElement x=\"100\" y=\"0\" width=\"200\" height=\"20\"/><textElement textAlignment=\"Center\" verticalAlignment=\"Middle\"/><textFieldExpression><![CDATA[$F{createdAt}]]></textFieldExpression></textField>" // No need for .toString()
                + "<textField><reportElement x=\"300\" y=\"0\" width=\"155\" height=\"20\"/><textElement textAlignment=\"Center\" verticalAlignment=\"Middle\"/><textFieldExpression><![CDATA[$F{tableNumber}]]></textFieldExpression></textField>"
                + "<textField><reportElement x=\"455\" y=\"0\" width=\"100\" height=\"20\"/><textElement textAlignment=\"Center\" verticalAlignment=\"Middle\"/><textFieldExpression><![CDATA[$F{totalAmount}]]></textFieldExpression></textField>"
                + "</band>"
                + "</detail>"
                + "</jasperReport>";
    }
}