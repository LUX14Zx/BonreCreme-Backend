package com.tlfdt.bonrecreme.controller.api.v1.manager.usecase.Report;

import com.tlfdt.bonrecreme.service.report.SalesReportService;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JRException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/manager/reports")
@RequiredArgsConstructor
public class GenerateReportController {

    private final SalesReportService salesReportService;

    @GetMapping("/excel")
    public ResponseEntity<byte[]> downloadMonthlyReportCSV(
            @RequestParam int year,
            @RequestParam int month) throws IOException {

        byte[] excelReport = salesReportService.generateSalesReportCSV(year, month);
        String fileName = "sales-report-" + year + "-" + month + ".csv";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelReport);
    }

    @GetMapping("/pdf")
    public ResponseEntity<byte[]> downloadMonthlyReportPDF(
            @RequestParam int year,
            @RequestParam int month) throws JRException {

        byte[] pdfReport = salesReportService.generateSalesReportPDF(year, month);
        String fileName = "sales-report-" + year + "-" + month + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfReport);
    }
}