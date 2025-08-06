package com.grababite.backend.services;

import com.grababite.backend.models.User;
import com.grababite.backend.models.College;
import com.grababite.backend.models.Cafeteria;
import com.grababite.backend.repositories.UserRepository;
import com.grababite.backend.repositories.CollegeRepository;
import com.grababite.backend.repositories.CafeteriaRepository;
import com.grababite.backend.exceptions.ResourceNotFoundException;
import com.grababite.backend.dto.UserResponse; // Import UserResponse DTO
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional; // Import Transactional

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Set;
import java.util.stream.Collectors; // Import Collectors

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CollegeRepository collegeRepository;

    @Autowired
    private CafeteriaRepository cafeteriaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Converts a User entity to a UserResponse DTO, populating associated college and cafeteria details.
     * @param user The User entity to convert.
     * @return A UserResponse DTO.
     */
    public UserResponse convertToUserResponse(User user) {
        UUID collegeId = (user.getCollege() != null) ? user.getCollege().getCollegeId() : null;
        String collegeName = (user.getCollege() != null) ? user.getCollege().getCollegeName() : null;
        UUID cafeteriaId = (user.getCafeteria() != null) ? user.getCafeteria().getCafeteriaId() : null;
        String cafeteriaName = (user.getCafeteria() != null) ? user.getCafeteria().getName() : null;

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRoles(),
                collegeId,
                collegeName,
                cafeteriaId,
                cafeteriaName
        );
    }

    /**
     * Retrieves a user by ID and returns it as a DTO.
     * @param id The UUID of the user.
     * @return An Optional containing the UserResponse DTO if found.
     */
    public Optional<UserResponse> getUserById(UUID id) {
        return userRepository.findById(id).map(this::convertToUserResponse);
    }

    /**
     * NEW: Retrieves a user by ID and returns the raw User entity.
     * This is needed when the full entity is required for internal business logic (e.g., setting relationships).
     * @param id The UUID of the user.
     * @return An Optional containing the User entity if found.
     */
    public Optional<User> getUserEntityById(UUID id) {
        return userRepository.findById(id);
    }

    /**
     * Retrieves a user by email and returns it as a DTO.
     * @param email The email of the user.
     * @return An Optional containing the UserResponse DTO if found.
     */
    public Optional<UserResponse> getUserByEmail(String email) {
        return userRepository.findByEmail(email).map(this::convertToUserResponse);
    }

    /**
     * NEW: Retrieves a user by email and returns the raw User entity.
     * This is needed when the full entity is required for internal business logic (e.g., setting relationships).
     * @param email The email of the user.
     * @return An Optional containing the User entity if found.
     */
    public Optional<User> getUserEntityByEmail(String email) {
        return userRepository.findByEmail(email);
    }


    /**
     * Retrieves a list of all users and returns them as DTOs.
     * @return A list of UserResponse DTOs.
     */
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToUserResponse)
                .collect(Collectors.toList());
    }

    /**
     * Updates an existing user's details.
     * This method allows updating name, email, password (if provided), roles,
     * and associated college/cafeteria.
     * @param id The UUID of the user to update.
     * @param userDetails The User object with updated details.
     * @return The updated UserResponse DTO.
     * @throws IllegalArgumentException if the updated email already exists for another user.
     * @throws ResourceNotFoundException if the specified college or cafeteria is not found.
     */
    @Transactional // Ensure this method is transactional for updates
    public UserResponse updateUser(UUID id, User userDetails) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        // Update basic fields if provided
        if (userDetails.getName() != null) {
            existingUser.setName(userDetails.getName());
        }
        if (userDetails.getEmail() != null && !userDetails.getEmail().equals(existingUser.getEmail())) {
            // Check if new email already exists for another user
            if (userRepository.findByEmail(userDetails.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Email already in use by another user.");
            }
            existingUser.setEmail(userDetails.getEmail());
        }
        // Update password only if a new one is provided
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }
        // Update roles if provided
        if (userDetails.getRoles() != null && !userDetails.getRoles().isEmpty()) {
            existingUser.setRoles(userDetails.getRoles());
        }

        // Update college association if provided
        if (userDetails.getCollege() != null && userDetails.getCollege().getCollegeId() != null) {
            College college = collegeRepository.findById(userDetails.getCollege().getCollegeId())
                    .orElseThrow(() -> new ResourceNotFoundException("College not found with id: " + userDetails.getCollege().getCollegeId()));
            existingUser.setCollege(college);
        } else if (userDetails.getCollege() != null && userDetails.getCollege().getCollegeId() == null) {
            // If college object is provided but ID is null, it means dissociate from college
            existingUser.setCollege(null);
        }

        // Update cafeteria association if provided
        if (userDetails.getCafeteria() != null && userDetails.getCafeteria().getCafeteriaId() != null) {
            Cafeteria cafeteria = cafeteriaRepository.findById(userDetails.getCafeteria().getCafeteriaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cafeteria not found with id: " + userDetails.getCafeteria().getCafeteriaId()));
            existingUser.setCafeteria(cafeteria);
        } else if (userDetails.getCafeteria() != null && userDetails.getCafeteria().getCafeteriaId() == null) {
            // If cafeteria object is provided but ID is null, it means dissociate from cafeteria
            existingUser.setCafeteria(null);
        }

        User updatedEntity = userRepository.save(existingUser);
        return convertToUserResponse(updatedEntity); // Convert to DTO before returning
    }

    /**
     * Deletes a user by their ID.
     * @param id The UUID of the user to delete.
     * @return true if the user was deleted, false otherwise.
     */
    public boolean deleteUser(UUID id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Helper method to get the cafeteriaId of the currently authenticated user.
     * Used for method-level security checks.
     * @return The UUID of the user's associated cafeteria, or null if not found or no cafeteria is linked.
     */
    public UUID getCurrentUserCafeteriaId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName(); // Get the authenticated user's email (principal)

        Optional<User> currentUserOptional = userRepository.findByEmail(userEmail);
        if (currentUserOptional.isPresent() && currentUserOptional.get().getCafeteria() != null) {
            return currentUserOptional.get().getCafeteria().getCafeteriaId();
        }
        return null;
    }
}
