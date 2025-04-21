package com.projectdata.transaction.controller;

import com.projectdata.transaction.dto.common.UserDTO;
import com.projectdata.transaction.dto.request.CreateUserRequest;
import com.projectdata.transaction.dto.response.ApiResponse;
import com.projectdata.transaction.exception.core.ResourceNotFoundException;
import com.projectdata.transaction.model.User;
import com.projectdata.transaction.model.UserRole;
import com.projectdata.transaction.model.UserStatus;
import com.projectdata.transaction.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/v1")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping(value = "/user")
    public List<User> findAll() {
        return userService.findAll();
    }

    @GetMapping(value = "/user/{id}")
    public User findById(@PathVariable Long id, HttpServletRequest request) {
        Optional<User> user = userService.findById(id);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new ResourceNotFoundException("User", id.toString(), request.getRequestURI());
        }
    }

    /*
     * @Create POST
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

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(userDTO, request.getRequestURI(), "User created successfully"));
    }

    @PutMapping("/user")
    public User update(@RequestBody User user) {
        return userService.save(user);
    }

    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @DeleteMapping(value = "/user/{id}")
    public void deleteById(@PathVariable Long id) {
        userService.deleteById(id);
    }

    @GetMapping("/find/userName/{userName}")
    public List<User> findByUserName(@PathVariable String userName) {
        return userService.findByUserName(userName);
    }
}
