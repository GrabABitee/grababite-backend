package com.grababite.backend.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.grababite.backend.dto.OnboardingRequest;
import com.grababite.backend.exceptions.ResourceNotFoundException;
import com.grababite.backend.models.College;
import com.grababite.backend.models.User;
import com.grababite.backend.repositories.CollegeRepository;
import com.grababite.backend.repositories.UserRepository;

@Service
public class OnboardingService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CollegeRepository collegeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User onboardUser(OnboardingRequest request) {
        // ✅ 1. Only check by authId if it's provided
        if (request.getAuthId() != null && !request.getAuthId().isBlank()) {
            Optional<User> existingUser = userRepository.findByAuthId(request.getAuthId());
            if (existingUser.isPresent()) {
                return existingUser.get(); // User already exists, return the existing one
            }
        }

        // ✅ 2. Validate college exists if collegeId is provided
        College college = null;
        if (request.getCollegeId() != null) {
            college = collegeRepository.findById(request.getCollegeId())
                    .orElseThrow(() -> new ResourceNotFoundException("College not found with id: " + request.getCollegeId()));
        }

        // ✅ 3. Create a new user
        User newUser = new User();
        newUser.setName(request.getName());
        newUser.setEmail(request.getEmail());
        newUser.setAuthId(request.getAuthId());

        // ✅ Encode and store password if provided
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        // ✅ Always default role = STUDENT (ignore client input)
        newUser.addRole("STUDENT");

        // ✅ Set the associated college (can be null)
        newUser.setCollege(college);

        // ✅ 4. Save and return the user
        return userRepository.save(newUser);
    }
}
