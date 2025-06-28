package com.tlfdt.bonrecreme.service.report;

import com.tlfdt.bonrecreme.controller.api.v1.manager.dto.report.SalesReportRequestDTO;
import net.sf.jasperreports.engine.JRException;

import java.io.IOException;

/**
 * A service interface for generating sales reports in various formats.
 */
public interface SalesReportService {

    /**
     * Generates a sales reports based on the provided request parameters.
     * <p>
     * This single method can produce reports in multiple formats (e.g., PDF, CSV)
     * by delegating to the appropriate implementation based on the format specified
     * in the {@link SalesReportRequestDTO}.
     *
     * @param request An object containing all necessary parameters for generating the reports,
     * such as the time period and the desired format.
     * @return A byte array containing the generated reports data.
     * @throws IOException  if an error occurs during file or stream processing (e.g., for CSV).
     * @throws JRException  if an error occurs during PDF generation with JasperReports.
     */
    byte[] generateSalesReport(SalesReportRequestDTO request) throws IOException, JRException;
}