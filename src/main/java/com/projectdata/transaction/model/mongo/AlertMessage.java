package com.projectdata.transaction.model.mongo;

import java.time.Instant;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.projectdata.transaction.model.AssetType;

import lombok.Data;

@Data
@Document(collection = "alert_messages")
public class AlertMessage {
    @Id
    private String id;
    private String userId;         // User receiving the alert
    private String walletAddress;  // Wallet that triggered the alert
    private String txHash;         // Related transaction hash
    private Double amount;
    private AssetType asset;
    private Instant timestamp;
    private String message;        // Human-readable alert message
    private Map<String, Object> metadata; // Any extra info (optional)
}