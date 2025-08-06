package com.grababite.backend.controllers;

import com.grababite.backend.models.User;
import com.grababite.backend.services.UserService;
import com.grababite.backend.exceptions.ResourceNotFoundException;
import com.grababite.backend.dto.UserResponse; // Import UserResponse DTO
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * GET /api/users
     * Retrieves a list of all users.
     * This endpoint should typically be restricted to ADMIN users.
     * @return A list of UserResponse DTOs.
     */
    @GetMapping
    public List<UserResponse> getAllUsers() { // Changed return type to List<UserResponse>
        return userService.getAllUsers(); // Service now returns DTOs
    }

    /**
     * GET /api/users/{id}
     * Retrieves a single user by their ID.
     * This endpoint should typically be restricted to ADMIN users, or the user themselves.
     * @param id The UUID of the user to retrieve.
     * @return ResponseEntity with the UserResponse DTO and HTTP status 200 OK,
     * or 404 Not Found if the user does not exist.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) { // Changed return type to UserResponse
        return userService.getUserById(id) // Use the service method that returns DTO
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * PUT /api/users/{id}
     * Updates an existing user's details.
     * This endpoint should typically be restricted to ADMIN users.
     * @param id The UUID of the user to update.
     * @param userDetails The User object with updated details (sent in request body).
     * @return ResponseEntity with the updated UserResponse DTO and HTTP status 200 OK,
     * or 404 Not Found if the user does not exist, or 400 Bad Request for invalid data.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable UUID id, @RequestBody User userDetails) { // Changed return type to UserResponse
        try {
            UserResponse updatedUser = userService.updateUser(id, userDetails); // Service now returns DTO
            return ResponseEntity.ok(updatedUser);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Use NOT_FOUND for resource not found
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // e.g., email already exists
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * DELETE /api/users/{id}
     * Deletes a user by their ID.
     * This endpoint should typically be restricted to ADMIN users.
     * @param id The UUID of the user to delete.
     * @return ResponseEntity with HTTP status 204 No Content if deleted,
     * or 404 Not Found if the user does not exist.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable UUID id) {
        boolean deleted = userService.deleteUser(id);
        if (deleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
