package com.tlfdt.bonrecreme.controller.api.v1.manager.report;

import com.tlfdt.bonrecreme.controller.api.v1.manager.dto.report.SalesReportRequestDTO;
import com.tlfdt.bonrecreme.service.report.SalesReportService;
import com.tlfdt.bonrecreme.service.report.enums.ReportFormat;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JRException;
import org.hibernate.validator.constraints.Range;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * REST controller for generating and downloading sales reports for managers.
 * Provides endpoints for both Excel (CSV) and PDF formats.
 */
@RestController
@RequestMapping("/api/v1/manager/reports")
@RequiredArgsConstructor
@Validated // Enables validation for request parameters
public class GenerateReportController {

    private final SalesReportService salesReportService;

    /**
     * Generates and streams a monthly sales reports in CSV format.
     *
     * @param year  The year of the reports (e.g., 2024). Must be a valid year.
     * @param month The month of the reports (1-12).
     * @return A ResponseEntity containing the CSV file as a byte array for download.
     * @throws IOException if an error occurs during file generation.
     * @throws JRException if there's an issue with the reporting engine.
     */
    @GetMapping("/excel")
    public ResponseEntity<byte[]> downloadMonthlyReportCSV(
            @RequestParam @Range(min = 2000, max = 2100, message = "Year must be between 2000 and 2100") int year,
            @RequestParam @Range(min = 1, max = 12, message = "Month must be between 1 and 12") int month) throws IOException, JRException {

        // Create a request object for the service layer
        SalesReportRequestDTO request = new SalesReportRequestDTO(year, month, ReportFormat.CSV);
        byte[] excelReport = salesReportService.generateSalesReport(request);
        String fileName = generateReportFilename(year, month, "csv");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(excelReport);
    }

    /**
     * Generates and streams a monthly sales reports in PDF format using JasperReports.
     *
     * @param year  The year of the reports (e.g., 2024). Must be a valid year.
     * @param month The month of the reports (1-12).
     * @return A ResponseEntity containing the PDF file as a byte array for download.
     * @throws JRException if an error occurs during JasperReports processing.
     * @throws IOException if there's an issue with file I/O.
     */
    @GetMapping("/pdf")
    public ResponseEntity<byte[]> downloadMonthlyReportPDF(
            @RequestParam @Range(min = 2000, max = 2100, message = "Year must be between 2000 and 2100") int year,
            @RequestParam @Range(min = 1, max = 12, message = "Month must be between 1 and 12") int month) throws JRException, IOException {

        // Create a request object for the service layer
        SalesReportRequestDTO request = new SalesReportRequestDTO(year, month, ReportFormat.PDF);
        byte[] pdfReport = salesReportService.generateSalesReport(request);
        String fileName = generateReportFilename(year, month, "pdf");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfReport);
    }

    /**
     * Helper method to create a standardized reports filename.
     *
     * @param year      The year of the reports.
     * @param month     The month of the reports.
     * @param extension The file extension (e.g., "csv", "pdf").
     * @return A formatted filename string.
     */
    private String generateReportFilename(int year, int month, String extension) {
        // Pads the month with a leading zero if necessary (e.g., 7 -> 07)
        String formattedMonth = String.format("%02d", month);
        return String.format("sales-report-%d-%s.%s", year, formattedMonth, extension);
    }
}
