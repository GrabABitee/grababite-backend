package com.grababite.backend.controllers;

import com.grababite.backend.dto.AuthResponse;
import com.grababite.backend.models.User;
import com.grababite.backend.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @GetMapping("/check")
    public ResponseEntity<AuthResponse> checkUser(@RequestParam String authId) {
        Optional<User> user = authService.findUserByAuthId(authId);
        if (user.isPresent()) {
            return ResponseEntity.ok(new AuthResponse("User authenticated successfully.", user.get()));
        } else {
            return ResponseEntity.ok(new AuthResponse("User not found, please onboard.", null));
        }
    }
}