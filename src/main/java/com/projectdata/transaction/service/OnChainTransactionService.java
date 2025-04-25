package com.projectdata.transaction.service;

import com.projectdata.transaction.dto.common.AlertDTO;
import com.projectdata.transaction.dto.common.TransactionDTO;
import com.projectdata.transaction.model.AssetType;
import com.projectdata.transaction.model.WalletChain;
import com.projectdata.transaction.model.mongo.RawTransaction;
import com.projectdata.transaction.repository.mongo.RawTransactionRepository;
import com.projectdata.transaction.service.kafka.TransactionAlertProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OnChainTransactionService {
    
    private final RawTransactionRepository rawTransactionRepository;
    private final TransactionAlertProducer alertProducer;
    private final MongoTemplate mongoTemplate;
    
    /**
     * Tracks a new on-chain transaction and generates alerts if necessary
     * 
     * @param transactionDTO The transaction DTO to track
     * @return The saved transaction DTO
     */
    public TransactionDTO trackTransaction(TransactionDTO transactionDTO) {
        log.info("Tracking new transaction: {}", transactionDTO.getTxHash());
        
        // Set timestamp if not provided
        if (transactionDTO.getTimestamp() == null) {
            transactionDTO.setTimestamp(Instant.now());
        }
        
        // Convert DTO to entity
        RawTransaction transaction = transactionDTO.toEntity();
        
        // Save the transaction to MongoDB
        RawTransaction savedTransaction = rawTransactionRepository.save(transaction);
        
        // Check if this transaction should trigger an alert
        if (shouldGenerateAlert(transactionDTO)) {
            generateAndSendAlert(transactionDTO);
        }
        
        // Convert entity back to DTO and return
        return TransactionDTO.fromEntity(savedTransaction);
    }
    
    /**
     * Determines if a transaction should generate an alert
     * This is a simple implementation - in a real system, you would have more complex rules
     * 
     * @param transactionDTO The transaction DTO to check
     * @return true if an alert should be generated
     */
    private boolean shouldGenerateAlert(TransactionDTO transactionDTO) {
        // Example rule: Generate alert for transactions over 1.0 ETH or 10.0 SOL
        if (transactionDTO.getAsset() == AssetType.ETH && transactionDTO.getAmount() >= 1.0) {
            return true;
        }
        
        if (transactionDTO.getAsset() == AssetType.SOL && transactionDTO.getAmount() >= 10.0) {
            return true;
        }
        
        // Example rule: Generate alert for any USDT transaction
        if (transactionDTO.getAsset() == AssetType.USDT) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Generates and sends an alert for a transaction
     * 
     * @param transactionDTO The transaction DTO to generate an alert for
     */
    private void generateAndSendAlert(TransactionDTO transactionDTO) {
        log.info("Generating alert for transaction: {}", transactionDTO.getTxHash());
        
        AlertDTO alertDTO = new AlertDTO();
        alertDTO.setWalletAddress(transactionDTO.getWalletAddress());
        alertDTO.setTxHash(transactionDTO.getTxHash());
        alertDTO.setAmount(transactionDTO.getAmount());
        alertDTO.setAsset(transactionDTO.getAsset());
        alertDTO.setTimestamp(Instant.now());
        
        // Create a human-readable message
        String message = String.format(
            "Transaction alert: %s %s detected on %s blockchain for wallet %s",
            transactionDTO.getAmount(),
            transactionDTO.getAsset(),
            transactionDTO.getBlockChain(),
            transactionDTO.getWalletAddress()
        );
        alertDTO.setMessage(message);
        
        // Add metadata
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("fromAddress", transactionDTO.getFromAddress());
        metadata.put("toAddress", transactionDTO.getToAddress());
        metadata.put("blockChain", transactionDTO.getBlockChain().name());
        alertDTO.setMetadata(metadata);
        
        // Convert DTO to entity and send the alert to Kafka
        alertProducer.sendAlert(alertDTO.toEntity());
    }
    
    /**
     * Retrieves a transaction by its ID
     * 
     * @param id The transaction ID
     * @return The transaction DTO, if found
     */
    public Optional<TransactionDTO> getTransactionById(String id) {
        return rawTransactionRepository.findById(id)
                .map(TransactionDTO::fromEntity);
    }
    
    /**
     * Retrieves transactions for a specific wallet address
     * 
     * @param walletAddress The wallet address
     * @param pageable Pagination information
     * @return A page of transaction DTOs
     */
    public Page<TransactionDTO> getTransactionsByWallet(String walletAddress, Pageable pageable) {
        Query query = new Query(Criteria.where("walletAddress").is(walletAddress))
                .with(pageable);
        
        long total = mongoTemplate.count(query, RawTransaction.class);
        List<RawTransaction> transactions = mongoTemplate.find(query, RawTransaction.class);
        
        // Convert entities to DTOs
        List<TransactionDTO> transactionDTOs = transactions.stream()
                .map(TransactionDTO::fromEntity)
                .collect(Collectors.toList());
        
        return new PageImpl<>(transactionDTOs, pageable, total);
    }
    
    /**
     * Retrieves transactions for a specific blockchain and wallet address
     * 
     * @param blockChain The blockchain
     * @param walletAddress The wallet address
     * @param pageable Pagination information
     * @return A page of transaction DTOs
     */
    public Page<TransactionDTO> getTransactionsByBlockchainAndWallet(
            WalletChain blockChain, 
            String walletAddress, 
            Pageable pageable) {
        
        Query query = new Query(Criteria.where("blockChain").is(blockChain)
                .and("walletAddress").is(walletAddress))
                .with(pageable);
        
        long total = mongoTemplate.count(query, RawTransaction.class);
        List<RawTransaction> transactions = mongoTemplate.find(query, RawTransaction.class);
        
        // Convert entities to DTOs
        List<TransactionDTO> transactionDTOs = transactions.stream()
                .map(TransactionDTO::fromEntity)
                .collect(Collectors.toList());
        
        return new PageImpl<>(transactionDTOs, pageable, total);
    }
}
