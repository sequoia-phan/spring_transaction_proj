package com.projectdata.transaction.dto.common;

import com.projectdata.transaction.model.AssetType;
import com.projectdata.transaction.model.mongo.AlertMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertDTO {
    private String id;
    private String userId;
    private String walletAddress;
    private String txHash;
    private Double amount;
    private AssetType asset;
    private Instant timestamp;
    private String message;
    private Map<String, Object> metadata;
    
    // Convert entity to DTO
    public static AlertDTO fromEntity(AlertMessage alert) {
        return AlertDTO.builder()
                .id(alert.getId())
                .userId(alert.getUserId())
                .walletAddress(alert.getWalletAddress())
                .txHash(alert.getTxHash())
                .amount(alert.getAmount())
                .asset(alert.getAsset())
                .timestamp(alert.getTimestamp())
                .message(alert.getMessage())
                .metadata(alert.getMetadata())
                .build();
    }
    
    // Convert DTO to entity
    public AlertMessage toEntity() {
        AlertMessage alert = new AlertMessage();
        alert.setId(this.id);
        alert.setUserId(this.userId);
        alert.setWalletAddress(this.walletAddress);
        alert.setTxHash(this.txHash);
        alert.setAmount(this.amount);
        alert.setAsset(this.asset);
        alert.setTimestamp(this.timestamp != null ? this.timestamp : Instant.now());
        alert.setMessage(this.message);
        alert.setMetadata(this.metadata);
        return alert;
    }
}
