package com.projectdata.transaction.dto.request;

import com.projectdata.transaction.model.WalletChain;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterWalletRequest {
    
    @NotNull(message = "Blockchain is required")
    private WalletChain blockChain;
    
    @NotBlank(message = "Wallet address is required")
    private String walletAddress;
    
    // Optional last processed transaction hash
    private String lastProcessedTx;
}
