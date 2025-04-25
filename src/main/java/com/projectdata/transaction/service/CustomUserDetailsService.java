package com.projectdata.transaction.service;

import com.projectdata.transaction.exception.core.ResourceNotFoundException;
import com.projectdata.transaction.model.User;
import com.projectdata.transaction.model.UserStatus;
import com.projectdata.transaction.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/*
 * @Implemented UserDetailsService to load user data from your database
 * @Added proper handling for user status verification
 * @Set up role-based authority mapping
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", username, null));

        // Check if user is active
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new UsernameNotFoundException("User is not active: " + username);
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUserName(),
                user.getPasswordHash(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())));
    }
}
