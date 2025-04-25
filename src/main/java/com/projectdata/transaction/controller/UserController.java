package com.projectdata.transaction.controller;

import com.projectdata.transaction.dto.common.AlertDTO;
import com.projectdata.transaction.dto.common.TransactionDTO;
import com.projectdata.transaction.dto.common.UserDTO;
import com.projectdata.transaction.dto.request.TrackTransactionRequest;
import com.projectdata.transaction.dto.response.ApiResponse;
import com.projectdata.transaction.dto.response.PageResponse;
import com.projectdata.transaction.exception.core.ResourceNotFoundException;
import com.projectdata.transaction.model.User;
import com.projectdata.transaction.model.WalletChain;
import com.projectdata.transaction.model.mongo.AlertMessage;
import com.projectdata.transaction.repository.mongo.AlertMessageRepository;
import com.projectdata.transaction.service.OnChainTransactionService;
import com.projectdata.transaction.service.UserService;
import com.projectdata.transaction.util.pagination.PaginationRequest;
import com.projectdata.transaction.util.pagination.PaginationUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/v1")
@Slf4j
public class UserController {

        @Autowired
        private UserService userService;

        @Autowired
        private OnChainTransactionService transactionService;

        @Autowired
        private AlertMessageRepository alertMessageRepository;

        /*
         * On-Chain Transaction Tracking APIs
         * 
         * /**
         * Track a new on-chain transaction and generate alerts if necessary
         */
        @PostMapping("/blockchain/track")
        public ResponseEntity<ApiResponse<TransactionDTO>> trackTransaction(
                        @Valid @RequestBody TrackTransactionRequest request,
                        HttpServletRequest httpRequest) {

                log.info("Received request to track transaction: {}", request.getTxHash());

                // Convert request to TransactionDTO
                TransactionDTO transactionDTO = new TransactionDTO();
                transactionDTO.setBlockChain(request.getBlockChain());
                transactionDTO.setTxHash(request.getTxHash());
                transactionDTO.setFromAddress(request.getFromAddress());
                transactionDTO.setToAddress(request.getToAddress());
                transactionDTO.setWalletAddress(request.getWalletAddress());
                transactionDTO.setAmount(request.getAmount());
                transactionDTO.setAsset(request.getAsset());
                transactionDTO.setRawData(request.getRawData());
                transactionDTO.setTimestamp(Instant.now());

                // Track the transaction (this will also send alerts if needed)
                TransactionDTO trackedTransaction = transactionService.trackTransaction(transactionDTO);

                return ResponseEntity.ok(ApiResponse.success(
                                trackedTransaction,
                                httpRequest.getRequestURI(),
                                "Transaction tracked successfully",
                                HttpStatus.OK));
        }

        /**
         * Get transaction by ID
         */
        @GetMapping("/blockchain/transaction/{id}")
        public ResponseEntity<ApiResponse<TransactionDTO>> getTransactionById(
                        @PathVariable String id,
                        HttpServletRequest request) {

                Optional<TransactionDTO> transaction = transactionService.getTransactionById(id);

                if (transaction.isEmpty()) {
                        throw new ResourceNotFoundException("Transaction", id, request.getRequestURI());
                }

                return ResponseEntity.ok(ApiResponse.success(
                                transaction.get(),
                                request.getRequestURI(),
                                "Transaction retrieved successfully",
                                HttpStatus.OK));
        }

        /**
         * Get transactions by wallet address
         */
        @GetMapping("/blockchain/wallet/{walletAddress}/transactions")
        public ResponseEntity<ApiResponse<Page<TransactionDTO>>> getTransactionsByWallet(
                        @PathVariable String walletAddress,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        @RequestParam(defaultValue = "timestamp") String sortBy,
                        @RequestParam(defaultValue = "desc") String direction,
                        HttpServletRequest request) {

                Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC
                                : Sort.Direction.DESC;

                Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

                Page<TransactionDTO> transactions = transactionService.getTransactionsByWallet(walletAddress, pageable);

                return ResponseEntity.ok(ApiResponse.success(
                                transactions,
                                request.getRequestURI(),
                                "Transactions retrieved successfully",
                                HttpStatus.OK));
        }

        /**
         * Get transactions by blockchain and wallet address
         */
        @GetMapping("/blockchain/{blockchain}/wallet/{walletAddress}/transactions")
        public ResponseEntity<ApiResponse<Page<TransactionDTO>>> getTransactionsByBlockchainAndWallet(
                        @PathVariable WalletChain blockchain,
                        @PathVariable String walletAddress,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        @RequestParam(defaultValue = "timestamp") String sortBy,
                        @RequestParam(defaultValue = "desc") String direction,
                        HttpServletRequest request) {

                Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC
                                : Sort.Direction.DESC;

                Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

                Page<TransactionDTO> transactions = transactionService.getTransactionsByBlockchainAndWallet(blockchain,
                                walletAddress, pageable);

                return ResponseEntity.ok(ApiResponse.success(
                                transactions,
                                request.getRequestURI(),
                                "Transactions retrieved successfully",
                                HttpStatus.OK));
        }

        /**
         * Get alerts for a user
         */
        @GetMapping("/alerts")
        public ResponseEntity<ApiResponse<Page<AlertDTO>>> getUserAlerts(
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        @RequestParam(defaultValue = "timestamp") String sortBy,
                        @RequestParam(defaultValue = "desc") String direction,
                        HttpServletRequest request) {

                // Get the current authenticated user
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                String username = authentication.getName();

                // Find the user by username
                User user = userService.findByUserName(username)
                                .orElseThrow(() -> new ResourceNotFoundException("User", username,
                                                request.getRequestURI()));

                Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC
                                : Sort.Direction.DESC;

                Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

                // Find alerts for this user
                Page<AlertMessage> alertsPage = alertMessageRepository.findByUserId(
                                user.getId().toString(), pageable);

                // Convert entities to DTOs
                Page<AlertDTO> alertDTOs = alertsPage.map(AlertDTO::fromEntity);

                return ResponseEntity.ok(ApiResponse.success(
                                alertDTOs,
                                request.getRequestURI(),
                                "Alerts retrieved successfully",
                                HttpStatus.OK));
        }

        /*
         * @GET
         * Get all users
         */
        @GetMapping(value = "/users")
        public ResponseEntity<ApiResponse<List<UserDTO>>> findAll(HttpServletRequest request) {
                List<User> list = userService.findAll();

                List<UserDTO> userDTOs = list.stream()
                                .map(user -> UserDTO.builder()
                                                .id(user.getId())
                                                .userName(user.getUserName())
                                                .email(user.getEmail())
                                                .role(user.getRole())
                                                .status(user.getStatus())
                                                .build())
                                .toList();

                return ResponseEntity.ok(ApiResponse.success(userDTOs, request.getRequestURI(), HttpStatus.OK));
        }

        /*
         * @GET
         * Get all users with pagination
         */
        @GetMapping(value = "/user")
        public ResponseEntity<ApiResponse<PageResponse<UserDTO>>> findAllPaginated(
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        @RequestParam(defaultValue = "id") String sortBy,
                        @RequestParam(defaultValue = "asc") String direction,
                        HttpServletRequest request) {

                PaginationRequest paginationRequest = PaginationUtil.buildPaginationRequest(page, size, sortBy,
                                direction);
                Page<User> userPage = userService.findAllPaginated(paginationRequest.toPageable());

                PageResponse<UserDTO> pageResponse = PaginationUtil.mapPageResponse(userPage,
                                user -> UserDTO.builder()
                                                .id(user.getId())
                                                .userName(user.getUserName())
                                                .email(user.getEmail())
                                                .role(user.getRole())
                                                .status(user.getStatus())
                                                .build());

                return ResponseEntity.ok(ApiResponse.success(pageResponse, request.getRequestURI(), HttpStatus.OK));
        }

        /*
         * @GET
         * Get user from specific id
         */
        @GetMapping(value = "/user/{id}")
        public ResponseEntity<ApiResponse<UserDTO>> findById(@PathVariable Long id, HttpServletRequest request) {
                User user = userService.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("User", id.toString(),
                                                request.getRequestURI()));

                UserDTO userDTO = UserDTO.builder()
                                .id(user.getId())
                                .userName(user.getUserName())
                                .email(user.getEmail())
                                .role(user.getRole())
                                .status(user.getStatus())
                                .build();

                return ResponseEntity.ok(ApiResponse.success(userDTO, request.getRequestURI(), HttpStatus.OK));
        }

        /*
         * @GET
         * Find users by userName
         */
        @GetMapping("/find/userName/{userName}")
        public ResponseEntity<ApiResponse<UserDTO>> findByUserName(@PathVariable String userName,
                        HttpServletRequest request) {
                User user = userService.findByUserName(userName)
                                .orElseThrow(() -> new ResourceNotFoundException("User", userName.toString(),
                                                request.getRequestURI()));

                UserDTO userDTO = UserDTO.builder()
                                .id(user.getId())
                                .userName(user.getUserName())
                                .email(user.getEmail())
                                .role(user.getRole())
                                .status(user.getStatus())
                                .build();

                return ResponseEntity.ok(ApiResponse.success(userDTO, request.getRequestURI(), HttpStatus.OK));
        }
}
