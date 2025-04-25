package com.projectdata.transaction.service;

import com.projectdata.transaction.model.User;
import com.projectdata.transaction.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> findAll() {
        // System.out.println("Service Users: " + userRepository.findAll()); 
        return userRepository.findAll();
    }

    public Page<User> findAllPaginated(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    public Optional<User> findByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }
    
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}

