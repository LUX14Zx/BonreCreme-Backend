package com.tlfdt.bonrecreme.service.report;

import com.tlfdt.bonrecreme.controller.api.v1.manager.dto.report.SalesReportRequestDTO;
import com.tlfdt.bonrecreme.exception.resource.ResourceNotFoundException;
import com.tlfdt.bonrecreme.model.restaurant.Bill;
import com.tlfdt.bonrecreme.model.restaurant.enums.BillStatus;
import com.tlfdt.bonrecreme.repository.restaurant.BillRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service implementation for generating sales reports.
 * This class handles the logic for fetching data and rendering it into different formats.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SalesReportServiceImpl implements SalesReportService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String JASPER_TEMPLATE_PATH = "reports/sales-report-template.jrxml";

    private final BillRepository billRepository;

    @Override
    @Transactional(value = "restaurantTransactionManager", readOnly = true)
    public byte[] generateSalesReport(SalesReportRequestDTO request) throws IOException, JRException {
        // Fetch data using the optimized repository method
        List<Bill> paidBills = findPaidBillsByYearMonth(request.year(), request.month());

        // Add this check
        if (paidBills.isEmpty()) {
            throw new ResourceNotFoundException("No paid bills found for the selected period.");
        }

        return switch (request.format()) {
            case CSV -> generateCsvReport(paidBills);
            case PDF -> generatePdfReport(paidBills, request.year(), request.month());
        };
    }

    /**
     * Fetches all paid bills for a specific year and month using an optimized repository query.
     * This prevents fetching the entire bills table into memory.
     *
     * @param year  The year of the report.
     * @param month The month of the report.
     * @return A list of paid bills for the specified period.
     */
    private List<Bill> findPaidBillsByYearMonth(int year, int month) {

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime startDate = yearMonth.atDay(1).atStartOfDay();
        // The end date is exclusive, so we start at the beginning of the next month.
        LocalDateTime endDate = yearMonth.atEndOfMonth().plusDays(1).atStartOfDay();

        log.info("Fetching paid bills between {} and {}", startDate, endDate);
        return billRepository.findBillsWithDetailsByStatusAndDateRange(BillStatus.PAID, startDate, endDate);
    }

    /**
     * Generates a sales report in CSV format from a list of bills.
     *
     * @param paidBills The list of paid bills to include in the report.
     * @return A byte array containing the report in CSV format.
     */
    private byte[] generateCsvReport(List<Bill> paidBills) {

        log.info("Generating CSV sales report for {} paid bills.", paidBills.size());

        StringBuilder csvBuilder = new StringBuilder();
        // Header
        csvBuilder.append("Date,Bill,Total Amount,\n");

        // Data Rows
        BigDecimal totalRevenue = BigDecimal.ZERO;
        for (Bill bill : paidBills)
        {
            csvBuilder.append(DATE_TIME_FORMATTER.format(bill.getCreatedAt())).append(",");
            csvBuilder.append(bill.getId()).append(",");
            csvBuilder.append(bill.getTotalAmount()).append("\n");

            totalRevenue = totalRevenue.add(bill.getTotalAmount());
        }

        // Add a blank line for separation
        csvBuilder.append("\n\n\n");

        // A clearer, left-aligned footer
        csvBuilder.append("Total Revenue,,")
                  .append(totalRevenue)
                  .append("\n");

        return csvBuilder.toString().getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Generates a sales report in PDF format using a JasperReports template.
     *
     * @param paidBills The list of paid bills to include in the report.
     * @param year      The report year, for the title.
     * @param month     The report month, for the title.
     * @return A byte array containing the report in PDF format.
     * @throws JRException if there is an error during JasperReports processing.
     * @throws IOException if the report template cannot be found.
     */
    private byte[] generatePdfReport(List<Bill> paidBills, int year, int month) throws JRException, IOException {
        log.info("Generating PDF sales report for {} paid bills using template: {}", paidBills.size(), JASPER_TEMPLATE_PATH);

        // 1. Load the JRXML template from the classpath
        InputStream reportStream = new ClassPathResource(JASPER_TEMPLATE_PATH).getInputStream();
        JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

        // 2. Prepare the data source
        List<Map<String, Object>> billMaps = paidBills.stream()
                .map(this::convertBillToMap)
                .collect(Collectors.toList());


        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(billMaps);

        // 3. Set report parameters

        // Calculate total sales
        BigDecimal totalSales = paidBills.stream()
                .map(Bill::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("ReportTitle", "Monthly Sales Report");
        parameters.put("MonthYear", Month.of(month).name() + " " + year);
        parameters.put("TotalSales", totalSales);

        // 4. Fill the report
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        // 5. Export to PDF
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }

    /**
     * Converts a Bill entity to a Map suitable for the JasperReports data source.
     *
     * @param bill The Bill entity to convert.
     * @return A map containing the bill data.
     */
    private Map<String, Object> convertBillToMap(Bill bill) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", bill.getId());
        map.put("createdAt", DATE_TIME_FORMATTER.format(bill.getCreatedAt()));
        map.put("totalAmount", bill.getTotalAmount());
        return map;
    }
}