package com.grababite.backend.controllers;

import com.grababite.backend.dto.LoginRequest;
import com.grababite.backend.models.User;
import com.grababite.backend.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * POST /api/auth/login
     * Handles user login.
     *
     * @param loginRequest DTO containing user's email and password.
     * @return ResponseEntity with the authenticated User object and HTTP status 200 OK,
     * or 401 Unauthorized if authentication fails.
     */
    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody LoginRequest loginRequest) {
        try {
            User authenticatedUser = authService.authenticateUser(loginRequest.getEmail(), loginRequest.getPassword());
            // Return the authenticated user object (excluding password for security)
            authenticatedUser.setPassword(null); // Clear password before sending to frontend
            return ResponseEntity.ok(authenticatedUser);
        } catch (UsernameNotFoundException | BadCredentialsException e) {
            // Handle incorrect credentials
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // 401 Unauthorized
        } catch (Exception e) {
            // Log other unexpected errors
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
        }
    }
}
