package com.projectdata.transaction.service.kafka;

import com.projectdata.transaction.model.mongo.AlertMessage;
import com.projectdata.transaction.repository.mongo.AlertMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionAlertConsumer {
    
    private final AlertMessageRepository alertMessageRepository;
    
    /**
     * Consumes alert messages from the Kafka topic and persists them to MongoDB
     * 
     * @param alertMessage The alert message received from Kafka
     */
    @KafkaListener(topics = "wallet-tracking", groupId = "transaction-alert-group")
    public void consumeAlert(AlertMessage alertMessage) {
        log.info("Received alert for transaction: {}", alertMessage.getTxHash());
        
        // Save the alert to MongoDB
        alertMessageRepository.save(alertMessage);
        
        log.info("Alert saved to database with ID: {}", alertMessage.getId());
    }
}
