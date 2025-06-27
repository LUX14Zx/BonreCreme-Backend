package com.tlfdt.bonrecreme.service.report;

import net.sf.jasperreports.engine.JRException;

import java.io.IOException;

public interface SalesReportService {
    byte[] generateSalesReportCSV(int year, int month) throws IOException;
    byte[] generateSalesReportPDF(int year, int month) throws JRException;
}