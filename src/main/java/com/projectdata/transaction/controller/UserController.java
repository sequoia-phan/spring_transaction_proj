package com.projectdata.transaction.controller;

import com.projectdata.transaction.model.User;
import com.projectdata.transaction.service.UserService;
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
    public Optional<User> findById(@PathVariable Long id) {
        return userService.findById(id);
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
