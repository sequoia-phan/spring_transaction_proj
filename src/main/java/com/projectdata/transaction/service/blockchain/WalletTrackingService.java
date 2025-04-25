package com.projectdata.transaction.service.blockchain;

import com.projectdata.transaction.dto.common.TransactionDTO;
import com.projectdata.transaction.model.WalletChain;
import com.projectdata.transaction.service.OnChainTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for tracking wallet transactions across different blockchains
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WalletTrackingService {

    private final BlockchainRpcClient rpcClient;
    private final OnChainTransactionService transactionService;
    
    // Map of blockchain -> (wallet address -> last processed transaction hash)
    private final Map<WalletChain, Map<String, String>> trackedWallets = new ConcurrentHashMap<>();
    
    // Set of transaction hashes we've already processed
    private final Set<String> processedTransactions = ConcurrentHashMap.newKeySet();
    
    /**
     * Registers a wallet for tracking
     * 
     * @param chain The blockchain
     * @param walletAddress The wallet address
     * @param lastProcessedTx Optional last processed transaction hash
     */
    public void registerWallet(WalletChain chain, String walletAddress, String lastProcessedTx) {
        log.info("Registering wallet {} on {} blockchain for tracking", walletAddress, chain);
        
        trackedWallets.computeIfAbsent(chain, k -> new ConcurrentHashMap<>())
                .put(walletAddress, lastProcessedTx);
    }
    
    /**
     * Unregisters a wallet from tracking
     * 
     * @param chain The blockchain
     * @param walletAddress The wallet address
     */
    public void unregisterWallet(WalletChain chain, String walletAddress) {
        log.info("Unregistering wallet {} on {} blockchain from tracking", walletAddress, chain);
        
        if (trackedWallets.containsKey(chain)) {
            trackedWallets.get(chain).remove(walletAddress);
        }
    }
    
    /**
     * Gets all tracked wallets
     * 
     * @return A map of blockchain -> set of wallet addresses
     */
    public Map<WalletChain, Set<String>> getTrackedWallets() {
        Map<WalletChain, Set<String>> result = new HashMap<>();
        
        trackedWallets.forEach((chain, wallets) -> 
            result.put(chain, new HashSet<>(wallets.keySet()))
        );
        
        return result;
    }
    
    /**
     * Manually fetches and processes a transaction
     * 
     * @param chain The blockchain
     * @param txHash The transaction hash
     * @return The processed transaction
     */
    public TransactionDTO fetchAndProcessTransaction(WalletChain chain, String txHash) {
        log.info("Manually fetching and processing transaction {} on {} blockchain", txHash, chain);
        
        // Skip if already processed
        if (processedTransactions.contains(txHash)) {
            log.info("Transaction {} already processed, skipping", txHash);
            return null;
        }
        
        // Fetch the transaction
        TransactionDTO transaction = rpcClient.getTransaction(chain, txHash);
        
        // Process the transaction
        TransactionDTO processedTx = transactionService.trackTransaction(transaction);
        
        // Mark as processed
        processedTransactions.add(txHash);
        
        return processedTx;
    }
    
    /**
     * Scheduled task to poll for new transactions for tracked wallets
     * In a real implementation, this would use blockchain-specific APIs to efficiently
     * fetch new transactions, possibly using websockets or other notification mechanisms
     */
    @Scheduled(fixedDelayString = "${blockchain.polling.interval:60000}")
    public void pollForNewTransactions() {
        log.info("Polling for new transactions for tracked wallets");
        
        trackedWallets.forEach((chain, wallets) -> {
            wallets.forEach((walletAddress, lastTxHash) -> {
                try {
                    // In a real implementation, you would use chain-specific APIs to fetch
                    // new transactions since lastTxHash. This is just a placeholder.
                    log.info("Checking for new transactions for wallet {} on {} blockchain", 
                            walletAddress, chain);
                    
                    // For demonstration purposes, we're not actually fetching new transactions here
                    // In a real implementation, you would:
                    // 1. Fetch new transactions for the wallet since lastTxHash
                    // 2. Process each transaction
                    // 3. Update the lastTxHash to the most recent transaction
                    
                } catch (Exception e) {
                    log.error("Error polling for new transactions for wallet {} on {} blockchain", 
                            walletAddress, chain, e);
                }
            });
        });
    }
}
