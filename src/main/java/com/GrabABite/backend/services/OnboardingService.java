package com.grababite.backend.services;

import com.grababite.backend.dto.OnboardingRequest;
import com.grababite.backend.exceptions.ResourceNotFoundException;
import com.grababite.backend.models.College;
import com.grababite.backend.models.User;
import com.grababite.backend.repositories.CollegeRepository;
import com.grababite.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OnboardingService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CollegeRepository collegeRepository;

    public User onboardUser(OnboardingRequest request) {
        // 1. Check if user already exists based on authId from Google
        Optional<User> existingUser = userRepository.findByAuthId(request.getAuthId());
        if (existingUser.isPresent()) {
            return existingUser.get(); // User already exists, return the existing user
        }

        // 2. Validate college exists if collegeId is provided
        College college = null;
        if (request.getCollegeId() != null) {
            college = collegeRepository.findById(request.getCollegeId())
                    .orElseThrow(() -> new ResourceNotFoundException("College not found with id: " + request.getCollegeId()));
        }

        // 3. Create a new user
        User newUser = new User();
        newUser.setName(request.getName());
        newUser.setEmail(request.getEmail());
        newUser.setAuthId(request.getAuthId());

        // CORRECTED: Use addRole for the Set<String> roles field
        if (request.getRole() != null && !request.getRole().isEmpty()) {
            newUser.addRole(request.getRole()); // Add the single role from the request
        } else {
            // Default role if none is provided, e.g., "STUDENT"
            newUser.addRole("STUDENT");
        }

        newUser.setCollege(college); // Set the associated college (can be null)

        // 4. Save the new user
        return userRepository.save(newUser);
    }
}
