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

        // 2. Validate college exists
        College college = collegeRepository.findById(request.getCollegeId())
                .orElseThrow(() -> new ResourceNotFoundException("College not found with id: " + request.getCollegeId()));

        // 3. Create a new user
        User newUser = new User();
        newUser.setName(request.getName());
        newUser.setEmail(request.getEmail());
        newUser.setAuthId(request.getAuthId());
        newUser.setRole(request.getRole());
        newUser.setCollege(college);

        // 4. Save the new user
        return userRepository.save(newUser);
    }
}