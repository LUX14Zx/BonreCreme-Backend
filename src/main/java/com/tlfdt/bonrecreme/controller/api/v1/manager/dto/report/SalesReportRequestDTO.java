package com.tlfdt.bonrecreme.controller.api.v1.manager.dto.report;

import com.tlfdt.bonrecreme.service.report.enums.ReportFormat;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Range;

/**
 * Represents the parameters for generating a sales reports.
 * Using a dedicated request object makes the service signature stable and extensible.
 *
 * @param year The year of the reports (e.g., 2024).
 * @param month The month of the reports (1-12).
 * @param format The desired {@link ReportFormat} (PDF or CSV).
 */
public record SalesReportRequestDTO(
        @Range(min = 2000, max = 2100, message = "Year must be between 2000 and 2100")
        int year,

        @Range(min = 1, max = 12, message = "Month must be between 1 and 12")
        int month,

        @NotNull(message = "Report format cannot be null.")
        ReportFormat format
) {}