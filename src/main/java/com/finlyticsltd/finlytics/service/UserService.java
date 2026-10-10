package com.finlyticsltd.finlytics.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.finlyticsltd.finlytics.dto.RegisterRequest;
import com.finlyticsltd.finlytics.dto.UserResponse;
import com.finlyticsltd.finlytics.entity.User;
import com.finlyticsltd.finlytics.exception.DuplicateResourceException;
import com.finlyticsltd.finlytics.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new DuplicateResourceException("Username already taken: " + request.username());
        }

        User user = new User();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));

        User saved = userRepository.save(user);
        return new UserResponse(saved.getId(), saved.getUsername());
    }
}