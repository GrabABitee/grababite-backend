package com.grababite.backend.controllers;

import com.grababite.backend.dto.AdminRegistrationRequest;
import com.grababite.backend.models.User;
import com.grababite.backend.services.AdminRegistrationService;
import org.springframework.beans.factory.annotation.Autowired; // CORRECTED IMPORT HERE
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin") // Base path for admin-related endpoints
public class AdminRegistrationController {

    @Autowired
    private AdminRegistrationService adminRegistrationService;

    /**
     * POST /api/admin/register
     * Endpoint for registering a new administrator user.
     * This endpoint is publicly accessible to allow initial admin setup.
     * @param request The AdminRegistrationRequest DTO containing registration details.
     * @return ResponseEntity with the created User object and HTTP status 201 Created.
     */
    @PostMapping("/register")
    public ResponseEntity<User> registerAdmin(@RequestBody AdminRegistrationRequest request) {
        try {
            User newAdmin = adminRegistrationService.registerAdmin(request);
            return new ResponseEntity<>(newAdmin, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            // Handle cases where user with email already exists
            return new ResponseEntity<>(HttpStatus.CONFLICT); // 409 Conflict
        } catch (Exception e) {
            // Generic error handling
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
        }
    }
}
