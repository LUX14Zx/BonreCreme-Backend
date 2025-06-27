package com.tlfdt.bonrecreme.service.report.masseging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tlfdt.bonrecreme.controller.api.v1.cashier.dto.bill.BillResponseDTO;
import com.tlfdt.bonrecreme.controller.api.v1.manager.view.ManagerReportMonitor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tlfdt.bonrecreme.service.order.masseging.KafkaProducerService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaidBillKafkaConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaidBillKafkaConsumer.class);
    private final ManagerReportMonitor managerReportMonitor;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "paid-bills-topic", groupId = "manager-group")
    public void consumePaidBill(String message) {
        try {
            BillResponseDTO bill = objectMapper.readValue(message, BillResponseDTO.class);
            LOGGER.info("Consumed paid bill for SSE: {}", bill.getBillId());
            managerReportMonitor.sendPaidBillNotification(bill);
        } catch (Exception err) {
            LOGGER.error("Failed to process paid bill message for SSE", err);
        }
    }
}