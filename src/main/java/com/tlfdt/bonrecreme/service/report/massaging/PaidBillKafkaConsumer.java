package com.tlfdt.bonrecreme.service.report.massaging;

import com.tlfdt.bonrecreme.controller.api.v1.cashier.dto.bill.BillResponseDTO;
import com.tlfdt.bonrecreme.service.report.sse.ReportSseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

/**
 * A Kafka consumer responsible for listening to events related to paid bills.
 * <p>
 * This service consumes messages from the "paid-bills-topic", deserializes them
 * into {@link BillResponseDTO} objects, and forwards them to the
 * {@link ReportSseService} to notify connected manager dashboards in real-time.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaidBillKafkaConsumer {

    private final ReportSseService reportSseService;

    /**
     * Consumes a message when a bill has been paid.
     * <p>
     * This listener is configured to automatically deserialize the incoming JSON message
     * into a {@link BillResponseDTO} object, thanks to the centrally configured
     * {@code ConcurrentKafkaListenerContainerFactory}. This eliminates manual parsing
     * and makes the process type-safe.
     *
     * @param bill The deserialized {@link BillResponseDTO} from the Kafka message payload.
     */
    @KafkaListener(
            topics = "paid-bills-topic",
            groupId = "manager-group",
            containerFactory = "kafkaListenerContainerFactory" // Ensure we use the factory with JSON support
    )
    public void consumePaidBill(@Payload BillResponseDTO bill) {
        try {
            log.info("Consumed 'bill-paid' event for Bill ID: #{}. Forwarding to SSE service.", bill.getBillId());
            // Forward the DTO to the SSE service to broadcast to manager clients
            reportSseService.sendPaidBillNotification(bill);
        } catch (Exception e) {
            // This catch block now only handles exceptions from the business logic (SSE sending)
            // Deserialization errors are handled by Spring Kafka's error handlers.
            log.error("Failed to process paid bill notification for Bill ID: #{}. Error: {}",
                    bill.getBillId(), e.getMessage());
        }
    }
}