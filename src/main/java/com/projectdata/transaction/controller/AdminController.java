package com.projectdata.transaction.controller;

import com.projectdata.transaction.dto.common.UserDTO;
import com.projectdata.transaction.dto.request.CreateUserRequest;
import com.projectdata.transaction.dto.response.ApiResponse;
import com.projectdata.transaction.model.User;
import com.projectdata.transaction.model.UserRole;
import com.projectdata.transaction.model.UserStatus;
import com.projectdata.transaction.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

        private final UserService userService;

        /*
         * @POST
         * Create new user
         */
        @PostMapping(value = "/user")
        public ResponseEntity<ApiResponse<UserDTO>> create(@Valid @RequestBody CreateUserRequest userRequest,
                        HttpServletRequest request) {
                // Custom validation example
                User user = User.builder()
                                .userName(userRequest.getUserName())
                                .email(userRequest.getEmail())
                                .passwordHash(userRequest.getPassword())
                                .status(UserStatus.ACTIVE)
                                .role(UserRole.USER)
                                .build();

                User savedUser = userService.save(user);

                UserDTO userDTO = UserDTO.builder()
                                .id(savedUser.getId())
                                .userName(savedUser.getUserName())
                                .email(savedUser.getEmail())
                                .role(savedUser.getRole())
                                .status(savedUser.getStatus())
                                .build();

                return ResponseEntity.ok(
                                ApiResponse.success(userDTO, request.getRequestURI(), "User created successfully",
                                                HttpStatus.CREATED));
        }

        /*
         * Update user's role via Id
         */
        @PatchMapping("/user/{id}/role")
        public ResponseEntity<ApiResponse<UserDTO>> updateUserRole(
                        @PathVariable Long id,
                        @RequestBody UserRole role,
                        HttpServletRequest request) {

                Optional<User> userOpt = userService.findById(id);

                if (userOpt.isEmpty()) {
                        return ResponseEntity.ok(ApiResponse.error("User not found", request.getRequestURI(),
                                        HttpStatus.NOT_FOUND));
                }

                User user = userOpt.get();
                user.setRole(role);
                User updatedUser = userService.save(user);

                UserDTO userDTO = UserDTO.builder()
                                .id(updatedUser.getId())
                                .userName(updatedUser.getUserName())
                                .email(updatedUser.getEmail())
                                .role(updatedUser.getRole())
                                .status(updatedUser.getStatus())
                                .build();

                return ResponseEntity.ok(ApiResponse.success(userDTO, request.getRequestURI(),
                                "User role updated successfully", HttpStatus.OK));
        }

        /*
         * @Delete user via Id
         */
        @DeleteMapping("/user/{id}")
        public ResponseEntity<ApiResponse<Map<String, Object>>> deleteUser(
                        @PathVariable Long id,
                        HttpServletRequest request) {

                Optional<User> userOpt = userService.findById(id);

                if (userOpt.isEmpty()) {
                        return ResponseEntity.ok(ApiResponse.error("User not found", request.getRequestURI(),
                                        HttpStatus.NOT_FOUND));
                }

                userService.deleteById(id);

                Map<String, Object> response = new HashMap<>();
                response.put("userId", id);
                response.put("deleted", true);

                return ResponseEntity.ok(ApiResponse.success(response, request.getRequestURI(),
                                "User deleted successfully", HttpStatus.OK));
        }
}
