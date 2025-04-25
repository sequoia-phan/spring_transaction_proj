package com.projectdata.transaction.controller;

import com.projectdata.transaction.dto.common.UserDTO;
import com.projectdata.transaction.dto.request.LoginRequest;
import com.projectdata.transaction.dto.request.CreateUserRequest;
import com.projectdata.transaction.dto.response.ApiResponse;
import com.projectdata.transaction.dto.response.JwtResponse;
import com.projectdata.transaction.exception.core.ResourceNotFoundException;
import com.projectdata.transaction.model.User;
import com.projectdata.transaction.model.UserRole;
import com.projectdata.transaction.model.UserStatus;
import com.projectdata.transaction.repository.UserRepository;
import com.projectdata.transaction.service.UserService;
import com.projectdata.transaction.util.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtResponse>> login(@Valid @RequestBody LoginRequest loginRequest,
            HttpServletRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUserName(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        User user = userRepository.findByUserName(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("auth", loginRequest.getUserName(),
                        request.getRequestURI()));
        String jwt = jwtUtil.generateToken(userDetails, user.getRole());

        JwtResponse jwtResponse = new JwtResponse(jwt, user.getId(), user.getUserName(), user.getEmail(),
                user.getRole());

        return ResponseEntity.ok(ApiResponse.success(jwtResponse, request.getRequestURI(), HttpStatus.OK));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDTO>> register(@Valid @RequestBody CreateUserRequest createUserRequest,
            HttpServletRequest request) {
        // Check if username already exists
        if (userRepository.findByUserName(createUserRequest.getUserName()) != null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Username is already taken", request.getRequestURI(),
                            HttpStatus.BAD_REQUEST));
        }

        // Check if email already exists
        if (userService.findByEmail(createUserRequest.getEmail()) != null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Email is already in use", request.getRequestURI(),
                            HttpStatus.BAD_REQUEST));
        }

        // Create new user
        User user = User.builder()
                .userName(createUserRequest.getUserName())
                .email(createUserRequest.getEmail())
                .passwordHash(passwordEncoder.encode(createUserRequest.getPassword()))
                .role(UserRole.USER) // Default role for new registrations
                .status(UserStatus.ACTIVE)
                .build();

        User savedUser = userRepository.save(user);

        UserDTO userDTO = UserDTO.builder()
                .id(savedUser.getId())
                .userName(savedUser.getUserName())
                .email(savedUser.getEmail())
                .role(savedUser.getRole())
                .status(savedUser.getStatus())
                .build();

        return ResponseEntity.ok(ApiResponse.success(userDTO, request.getRequestURI(), HttpStatus.CREATED));
    }
}
