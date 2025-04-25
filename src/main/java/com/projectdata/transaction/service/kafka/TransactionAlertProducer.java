package com.projectdata.transaction.service.kafka;

import com.projectdata.transaction.model.mongo.AlertMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionAlertProducer {
    
    private final KafkaTemplate<String, AlertMessage> kafkaTemplate;
    
    /**
     * Sends an alert message to the Kafka topic
     * 
     * @param alertMessage The alert message to send
     */
    public void sendAlert(AlertMessage alertMessage) {
        log.info("Sending alert for transaction: {}", alertMessage.getTxHash());
        kafkaTemplate.send("wallet-tracking", alertMessage.getWalletAddress(), alertMessage);
        log.info("Alert sent successfully");
    }
}
