package com.projectdata.transaction.controller;

import com.projectdata.transaction.exception.common.NotFoundException;
import com.projectdata.transaction.model.User;
import com.projectdata.transaction.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/")
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
            throw new NotFoundException("User with ID " + id + " not found", request.getRequestURI());
        }
    }

    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping(value = "/user")
    public User create(@RequestBody User user) {
        return userService.save(user);
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
