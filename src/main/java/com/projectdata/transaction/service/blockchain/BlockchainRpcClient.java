package com.projectdata.transaction.service.blockchain;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.projectdata.transaction.dto.common.TransactionDTO;
import com.projectdata.transaction.model.AssetType;
import com.projectdata.transaction.model.WalletChain;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Client for making JSON-RPC requests to different blockchains
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BlockchainRpcClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    @Value("${blockchain.rpc.ethereum}")
    private String ethereumRpcUrl;
    
    @Value("${blockchain.rpc.solana}")
    private String solanaRpcUrl;
    
    @Value("${blockchain.rpc.bsc}")
    private String bscRpcUrl;
    
    @Value("${blockchain.rpc.sui}")
    private String suiRpcUrl;
    
    /**
     * Fetches a transaction from the blockchain by its hash
     * 
     * @param chain The blockchain to query
     * @param txHash The transaction hash
     * @return A DTO containing the transaction data
     */
    public TransactionDTO getTransaction(WalletChain chain, String txHash) {
        log.info("Fetching transaction {} from {} blockchain", txHash, chain);
        
        switch (chain) {
            case ETH:
                return getEthereumTransaction(txHash);
            case SOL:
                return getSolanaTransaction(txHash);
            case BSC:
                return getBscTransaction(txHash);
            case SUI:
                return getSuiTransaction(txHash);
            default:
                throw new IllegalArgumentException("Unsupported blockchain: " + chain);
        }
    }
    
    /**
     * Fetches an Ethereum transaction by its hash
     */
    private TransactionDTO getEthereumTransaction(String txHash) {
        try {
            // Create JSON-RPC request body
            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("jsonrpc", "2.0");
            requestBody.put("method", "eth_getTransactionByHash");
            requestBody.put("id", 1);
            requestBody.putArray("params").add(txHash);
            
            // Make the request
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(requestBody), headers);
            
            String response = restTemplate.postForObject(ethereumRpcUrl, request, String.class);
            JsonNode jsonResponse = objectMapper.readTree(response);
            
            // Extract transaction data
            JsonNode result = jsonResponse.get("result");
            if (result == null || result.isNull()) {
                throw new RuntimeException("Transaction not found: " + txHash);
            }
            
            // Parse transaction data
            String from = result.get("from").asText();
            String to = result.get("to").asText();
            
            // Convert hex value to decimal
            String valueHex = result.get("value").asText();
            BigInteger valueWei = new BigInteger(valueHex.substring(2), 16);
            BigDecimal valueEth = new BigDecimal(valueWei).divide(new BigDecimal("1000000000000000000"));
            
            // Build the DTO
            TransactionDTO dto = new TransactionDTO();
            dto.setBlockChain(WalletChain.ETH);
            dto.setTxHash(txHash);
            dto.setFromAddress(from);
            dto.setToAddress(to);
            dto.setWalletAddress(to); // Assuming recipient is the wallet we're tracking
            dto.setAmount(valueEth.doubleValue());
            dto.setAsset(AssetType.ETH);
            dto.setTimestamp(Instant.now()); // Ethereum doesn't include timestamp in tx, would need block data
            
            // Store raw data for reference
            Map<String, Object> rawData = new HashMap<>();
            rawData.put("gasPrice", result.get("gasPrice").asText());
            rawData.put("gasUsed", result.get("gas").asText());
            rawData.put("nonce", result.get("nonce").asText());
            dto.setRawData(rawData);
            
            return dto;
            
        } catch (Exception e) {
            log.error("Error fetching Ethereum transaction: {}", txHash, e);
            throw new RuntimeException("Failed to fetch Ethereum transaction: " + e.getMessage(), e);
        }
    }
    
    /**
     * Fetches a Solana transaction by its hash
     */
    private TransactionDTO getSolanaTransaction(String txHash) {
        try {
            // Create JSON-RPC request body
            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("jsonrpc", "2.0");
            requestBody.put("method", "getTransaction");
            requestBody.put("id", 1);
            ObjectNode params = requestBody.putObject("params");
            params.put("signature", txHash);
            params.put("encoding", "json");
            
            // Make the request
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(requestBody), headers);
            
            String response = restTemplate.postForObject(solanaRpcUrl, request, String.class);
            JsonNode jsonResponse = objectMapper.readTree(response);
            
            // Extract transaction data
            JsonNode result = jsonResponse.get("result");
            if (result == null || result.isNull()) {
                throw new RuntimeException("Transaction not found: " + txHash);
            }
            
            // Parse transaction data - this is simplified, real Solana tx parsing is more complex
            JsonNode meta = result.get("meta");
            JsonNode transaction = result.get("transaction");
            JsonNode message = transaction.get("message");
            
            // In a real implementation, you would need to decode the instruction data
            // and match account indices to actual addresses
            String from = message.get("accountKeys").get(0).asText();
            String to = message.get("accountKeys").get(1).asText();
            
            // For simplicity, we're using a placeholder amount
            // In a real implementation, you would parse the token transfer amount
            double amount = 1.0; // Placeholder
            
            // Build the DTO
            TransactionDTO dto = new TransactionDTO();
            dto.setBlockChain(WalletChain.SOL);
            dto.setTxHash(txHash);
            dto.setFromAddress(from);
            dto.setToAddress(to);
            dto.setWalletAddress(to); // Assuming recipient is the wallet we're tracking
            dto.setAmount(amount);
            dto.setAsset(AssetType.SOL);
            
            // Get timestamp if available
            if (result.has("blockTime")) {
                long blockTime = result.get("blockTime").asLong();
                dto.setTimestamp(Instant.ofEpochSecond(blockTime));
            } else {
                dto.setTimestamp(Instant.now());
            }
            
            // Store raw data for reference
            Map<String, Object> rawData = new HashMap<>();
            rawData.put("fee", meta.get("fee").asText());
            rawData.put("slot", result.get("slot").asText());
            dto.setRawData(rawData);
            
            return dto;
            
        } catch (Exception e) {
            log.error("Error fetching Solana transaction: {}", txHash, e);
            throw new RuntimeException("Failed to fetch Solana transaction: " + e.getMessage(), e);
        }
    }
    
    /**
     * Fetches a BSC transaction by its hash
     * BSC uses the same format as Ethereum
     */
    private TransactionDTO getBscTransaction(String txHash) {
        try {
            // Create JSON-RPC request body
            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("jsonrpc", "2.0");
            requestBody.put("method", "eth_getTransactionByHash");
            requestBody.put("id", 1);
            requestBody.putArray("params").add(txHash);
            
            // Make the request
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(requestBody), headers);
            
            String response = restTemplate.postForObject(bscRpcUrl, request, String.class);
            JsonNode jsonResponse = objectMapper.readTree(response);
            
            // Extract transaction data
            JsonNode result = jsonResponse.get("result");
            if (result == null || result.isNull()) {
                throw new RuntimeException("Transaction not found: " + txHash);
            }
            
            // Parse transaction data
            String from = result.get("from").asText();
            String to = result.get("to").asText();
            
            // Convert hex value to decimal
            String valueHex = result.get("value").asText();
            BigInteger valueWei = new BigInteger(valueHex.substring(2), 16);
            BigDecimal valueBnb = new BigDecimal(valueWei).divide(new BigDecimal("1000000000000000000"));
            
            // Build the DTO
            TransactionDTO dto = new TransactionDTO();
            dto.setBlockChain(WalletChain.BSC);
            dto.setTxHash(txHash);
            dto.setFromAddress(from);
            dto.setToAddress(to);
            dto.setWalletAddress(to); // Assuming recipient is the wallet we're tracking
            dto.setAmount(valueBnb.doubleValue());
            dto.setAsset(AssetType.BTC); // Using BTC as a placeholder, would need proper token detection
            dto.setTimestamp(Instant.now()); // BSC doesn't include timestamp in tx, would need block data
            
            // Store raw data for reference
            Map<String, Object> rawData = new HashMap<>();
            rawData.put("gasPrice", result.get("gasPrice").asText());
            rawData.put("gasUsed", result.get("gas").asText());
            rawData.put("nonce", result.get("nonce").asText());
            dto.setRawData(rawData);
            
            return dto;
            
        } catch (Exception e) {
            log.error("Error fetching BSC transaction: {}", txHash, e);
            throw new RuntimeException("Failed to fetch BSC transaction: " + e.getMessage(), e);
        }
    }
    
    /**
     * Fetches a SUI transaction by its hash
     */
    private TransactionDTO getSuiTransaction(String txHash) {
        try {
            // Create JSON-RPC request body
            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("jsonrpc", "2.0");
            requestBody.put("method", "sui_getTransaction");
            requestBody.put("id", 1);
            requestBody.putArray("params").add(txHash);
            
            // Make the request
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(requestBody), headers);
            
            String response = restTemplate.postForObject(suiRpcUrl, request, String.class);
            JsonNode jsonResponse = objectMapper.readTree(response);
            
            // Extract transaction data
            JsonNode result = jsonResponse.get("result");
            if (result == null || result.isNull()) {
                throw new RuntimeException("Transaction not found: " + txHash);
            }
            
            // Parse transaction data - this is simplified, real SUI tx parsing is more complex
            JsonNode transaction = result.get("transaction");
            JsonNode data = transaction.get("data");
            
            // In a real implementation, you would need to decode the transaction data
            // and extract the sender, recipient, and amount
            String from = data.get("sender").asText();
            String to = "unknown"; // Placeholder, would need to extract from tx data
            
            // For simplicity, we're using a placeholder amount
            double amount = 1.0; // Placeholder
            
            // Build the DTO
            TransactionDTO dto = new TransactionDTO();
            dto.setBlockChain(WalletChain.SUI);
            dto.setTxHash(txHash);
            dto.setFromAddress(from);
            dto.setToAddress(to);
            dto.setWalletAddress(to); // Assuming recipient is the wallet we're tracking
            dto.setAmount(amount);
            dto.setAsset(AssetType.BTC); // Using BTC as a placeholder, would need proper token detection
            
            // Get timestamp if available
            if (result.has("timestampMs")) {
                long timestampMs = result.get("timestampMs").asLong();
                dto.setTimestamp(Instant.ofEpochMilli(timestampMs));
            } else {
                dto.setTimestamp(Instant.now());
            }
            
            // Store raw data for reference
            Map<String, Object> rawData = new HashMap<>();
            rawData.put("digest", transaction.get("digest").asText());
            rawData.put("effects", result.get("effects").toString());
            dto.setRawData(rawData);
            
            return dto;
            
        } catch (Exception e) {
            log.error("Error fetching SUI transaction: {}", txHash, e);
            throw new RuntimeException("Failed to fetch SUI transaction: " + e.getMessage(), e);
        }
    }
}
