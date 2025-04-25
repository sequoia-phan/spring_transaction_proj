package com.projectdata.transaction.dto.request;

import com.projectdata.transaction.model.AssetType;
import com.projectdata.transaction.model.WalletChain;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrackTransactionRequest {
    
    @NotNull(message = "Blockchain is required")
    private WalletChain blockChain;
    
    @NotBlank(message = "Transaction hash is required")
    private String txHash;
    
    @NotBlank(message = "From address is required")
    private String fromAddress;
    
    @NotBlank(message = "To address is required")
    private String toAddress;
    
    @NotBlank(message = "Wallet address is required")
    private String walletAddress;
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private Double amount;
    
    @NotNull(message = "Asset type is required")
    private AssetType asset;
    
    private Map<String, Object> rawData;
}
