package com.grababite.backend.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grababite.backend.dto.UserResponse;
import com.grababite.backend.exceptions.ResourceNotFoundException;
import com.grababite.backend.models.Cafeteria;
import com.grababite.backend.models.College;
import com.grababite.backend.models.User;
import com.grababite.backend.repositories.CafeteriaRepository;
import com.grababite.backend.repositories.CollegeRepository;
import com.grababite.backend.repositories.UserRepository;

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

    // ✅ Convert User entity to UserResponse
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

    // ✅ Get logged-in user’s profile
    public UserResponse getCurrentUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        return convertToUserResponse(user);
    }

    // ✅ Update logged-in user’s own profile
    @Transactional
    public UserResponse updateCurrentUser(User userDetails) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        // Personal info updates
        if (userDetails.getName() != null) {
            existingUser.setName(userDetails.getName());
        }
        if (userDetails.getEmail() != null && !userDetails.getEmail().equals(existingUser.getEmail())) {
            if (userRepository.findByEmail(userDetails.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Email already in use by another user.");
            }
            existingUser.setEmail(userDetails.getEmail());
        }
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }

        // ✅ Students can set college only once (during onboarding)
        if (userDetails.getCollege() != null && userDetails.getCollege().getCollegeId() != null) {
            if (existingUser.getCollege() == null) { // allow only if not already set
                College college = collegeRepository.findById(userDetails.getCollege().getCollegeId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "College not found with id: " + userDetails.getCollege().getCollegeId()));
                existingUser.setCollege(college);
            }
        }

        // ✅ Students can update cafeteria anytime
        if (userDetails.getCafeteria() != null && userDetails.getCafeteria().getCafeteriaId() != null) {
            Cafeteria cafeteria = cafeteriaRepository.findById(userDetails.getCafeteria().getCafeteriaId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Cafeteria not found with id: " + userDetails.getCafeteria().getCafeteriaId()));
            existingUser.setCafeteria(cafeteria);
        }

        User updated = userRepository.save(existingUser);
        return convertToUserResponse(updated);
    }

    // ✅ Utility methods
    public Optional<User> getUserEntityByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User getUserEntityOrThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    // ✅ ADMIN endpoints
    public Optional<UserResponse> getUserById(UUID id) {
        return userRepository.findById(id).map(this::convertToUserResponse);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToUserResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserResponse updateUser(UUID id, User userDetails) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        if (userDetails.getName() != null) {
            existingUser.setName(userDetails.getName());
        }
        if (userDetails.getEmail() != null && !userDetails.getEmail().equals(existingUser.getEmail())) {
            if (userRepository.findByEmail(userDetails.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Email already in use by another user.");
            }
            existingUser.setEmail(userDetails.getEmail());
        }
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }
        if (userDetails.getRoles() != null && !userDetails.getRoles().isEmpty()) {
            existingUser.setRoles(userDetails.getRoles());
        }
        if (userDetails.getCollege() != null && userDetails.getCollege().getCollegeId() != null) {
            College college = collegeRepository.findById(userDetails.getCollege().getCollegeId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "College not found with id: " + userDetails.getCollege().getCollegeId()));
            existingUser.setCollege(college);
        }
        if (userDetails.getCafeteria() != null && userDetails.getCafeteria().getCafeteriaId() != null) {
            Cafeteria cafeteria = cafeteriaRepository.findById(userDetails.getCafeteria().getCafeteriaId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Cafeteria not found with id: " + userDetails.getCafeteria().getCafeteriaId()));
            existingUser.setCafeteria(cafeteria);
        }

        User updated = userRepository.save(existingUser);
        return convertToUserResponse(updated);
    }

    public boolean deleteUser(UUID id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
