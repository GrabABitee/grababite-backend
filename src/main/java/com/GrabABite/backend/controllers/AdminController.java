package com.grababite.backend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.grababite.backend.dto.AdminRegistrationRequest;
import com.grababite.backend.models.User;
import com.grababite.backend.repositories.UserRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid AdminRegistrationRequest req,
                                      Authentication authentication) {

        // If an admin already exists, only an authenticated ADMIN may create more admins
        boolean adminExists = userRepository.existsByRole("ADMIN");
        if (adminExists) {
            boolean callerIsAdmin = authentication != null &&
                authentication.getAuthorities().stream()
                    .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
            if (!callerIsAdmin) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Only ADMIN can create another ADMIN.");
            }
        }

        // Prevent duplicate email
        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Email already in use.");
        }

        // Create admin with encoded password
        User u = new User();
        u.setName(req.getName());
        u.setEmail(req.getEmail());
        u.setPassword(passwordEncoder.encode(req.getPassword())); // <-- important
        u.addRole("ADMIN");

        // If you store createdAt/authId/etc., set them here as well.

        User saved = userRepository.save(u);

        // Return a safe projection (don’t return password)
        return ResponseEntity.ok(new java.util.HashMap<>() {{
            put("id", saved.getId());
            put("name", saved.getName());
            put("email", saved.getEmail());
            put("roles", saved.getRoles());
            put("createdAt", saved.getCreatedAt());
        }});
    }
}
