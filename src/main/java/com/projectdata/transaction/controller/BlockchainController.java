package com.projectdata.transaction.controller;

import com.projectdata.transaction.dto.common.TransactionDTO;
import com.projectdata.transaction.dto.request.RegisterWalletRequest;
import com.projectdata.transaction.dto.response.ApiResponse;
import com.projectdata.transaction.model.WalletChain;
import com.projectdata.transaction.service.blockchain.WalletTrackingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/blockchain")
@RequiredArgsConstructor
@Slf4j
public class BlockchainController {

    private final WalletTrackingService walletTrackingService;

    /**
     * Register a wallet for tracking
     */
    @PostMapping("/wallet/register")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<ApiResponse<Void>> registerWallet(
            @Valid @RequestBody RegisterWalletRequest request,
            HttpServletRequest httpRequest) {
        
        log.info("Registering wallet {} on {} blockchain for tracking", 
                request.getWalletAddress(), request.getBlockChain());
        
        walletTrackingService.registerWallet(
                request.getBlockChain(), 
                request.getWalletAddress(), 
                request.getLastProcessedTx());
        
        return ResponseEntity.ok(ApiResponse.success(
                null,
                httpRequest.getRequestURI(),
                "Wallet registered for tracking successfully",
                HttpStatus.OK));
    }
    
    /**
     * Unregister a wallet from tracking
     */
    @DeleteMapping("/wallet/{chain}/{address}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<ApiResponse<Void>> unregisterWallet(
            @PathVariable WalletChain chain,
            @PathVariable @NotBlank String address,
            HttpServletRequest httpRequest) {
        
        log.info("Unregistering wallet {} on {} blockchain from tracking", address, chain);
        
        walletTrackingService.unregisterWallet(chain, address);
        
        return ResponseEntity.ok(ApiResponse.success(
                null,
                httpRequest.getRequestURI(),
                "Wallet unregistered from tracking successfully",
                HttpStatus.OK));
    }
    
    /**
     * Get all tracked wallets
     */
    @GetMapping("/wallets")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<ApiResponse<Map<WalletChain, Set<String>>>> getTrackedWallets(
            HttpServletRequest httpRequest) {
        
        log.info("Getting all tracked wallets");
        
        Map<WalletChain, Set<String>> trackedWallets = walletTrackingService.getTrackedWallets();
        
        return ResponseEntity.ok(ApiResponse.success(
                trackedWallets,
                httpRequest.getRequestURI(),
                "Tracked wallets retrieved successfully",
                HttpStatus.OK));
    }
    
    /**
     * Manually fetch and process a transaction
     */
    @PostMapping("/transaction/{chain}/{txHash}/process")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<ApiResponse<TransactionDTO>> fetchAndProcessTransaction(
            @PathVariable WalletChain chain,
            @PathVariable @NotBlank String txHash,
            HttpServletRequest httpRequest) {
        
        log.info("Manually fetching and processing transaction {} on {} blockchain", txHash, chain);
        
        TransactionDTO transaction = walletTrackingService.fetchAndProcessTransaction(chain, txHash);
        
        return ResponseEntity.ok(ApiResponse.success(
                transaction,
                httpRequest.getRequestURI(),
                "Transaction processed successfully",
                HttpStatus.OK));
    }
}
