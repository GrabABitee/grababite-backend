package com.grababite.backend.services;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.grababite.backend.config.JwtUtil;
import com.grababite.backend.dto.AuthResponse;
import com.grababite.backend.models.User;
import com.grababite.backend.repositories.UserRepository;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public AuthService(AuthenticationManager authenticationManager,
                       UserRepository userRepository,
                       JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse login(String email, String password) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
    
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    
        // Get first role from Set
        String role = user.getRoles().stream().findFirst()
                .orElseThrow(() -> new RuntimeException("No role assigned"));
    
        String token = jwtUtil.generateToken(email, role);
    
        return new AuthResponse(token, role, user.getId());
    }
}
