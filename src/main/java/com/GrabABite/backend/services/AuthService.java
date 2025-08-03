package com.grababite.backend.services;

import com.grababite.backend.models.User;
import com.grababite.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    public Optional<User> findUserByAuthId(String authId) {
        return userRepository.findByAuthId(authId);
    }
}