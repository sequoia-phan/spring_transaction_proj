package com.projectdata.transaction.dto.common;

import com.projectdata.transaction.model.AssetType;
import com.projectdata.transaction.model.WalletChain;
import com.projectdata.transaction.model.mongo.RawTransaction;
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
public class TransactionDTO {
    private String id;
    private WalletChain blockChain;
    private String txHash;
    private String fromAddress;
    private String toAddress;
    private String walletAddress;
    private Double amount;
    private AssetType asset;
    private Instant timestamp;
    private Map<String, Object> rawData;
    
    // Convert entity to DTO
    public static TransactionDTO fromEntity(RawTransaction transaction) {
        return TransactionDTO.builder()
                .id(transaction.getId())
                .blockChain(transaction.getBlockChain())
                .txHash(transaction.getTxHash())
                .fromAddress(transaction.getFromAddress())
                .toAddress(transaction.getToAddress())
                .walletAddress(transaction.getWalletAddress())
                .amount(transaction.getAmount())
                .asset(transaction.getAsset())
                .timestamp(transaction.getTimestamp())
                .rawData(transaction.getRawData())
                .build();
    }
    
    // Convert DTO to entity
    public RawTransaction toEntity() {
        RawTransaction transaction = new RawTransaction();
        transaction.setId(this.id);
        transaction.setBlockChain(this.blockChain);
        transaction.setTxHash(this.txHash);
        transaction.setFromAddress(this.fromAddress);
        transaction.setToAddress(this.toAddress);
        transaction.setWalletAddress(this.walletAddress);
        transaction.setAmount(this.amount);
        transaction.setAsset(this.asset);
        transaction.setTimestamp(this.timestamp != null ? this.timestamp : Instant.now());
        transaction.setRawData(this.rawData);
        return transaction;
    }
}
