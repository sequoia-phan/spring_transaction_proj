package com.projectdata.transaction.model.mongo;

import java.time.Instant;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.projectdata.transaction.model.AssetType;
import com.projectdata.transaction.model.WalletChain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "raw_transactions")
public class RawTransaction {
    @Id
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
}
