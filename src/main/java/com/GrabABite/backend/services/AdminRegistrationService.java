package com.grababite.backend.services;

import com.grababite.backend.dto.AdminRegistrationRequest;
// import com.grababite.backend.exceptions.ResourceNotFoundException; // No longer needed for college check
import com.grababite.backend.models.College; // Still needed if User entity has a College field, but not for direct linking here
import com.grababite.backend.models.User;
// import com.grababite.backend.repositories.CollegeRepository; // No longer needed for admin registration
import com.grababite.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
// import java.util.Optional; // No longer needed for college check
import java.util.Set;

@Service
public class AdminRegistrationService {

    @Autowired
    private UserRepository userRepository;

    // Removed: @Autowired private CollegeRepository collegeRepository; // Admin registration no longer links to college

    @Autowired
    private PasswordEncoder passwordEncoder; // To hash the password

    /**
     * Registers a new administrator user.
     * @param request The AdminRegistrationRequest containing user details.
     * @return The newly created User object.
     * @throws IllegalArgumentException if a user with the given email already exists.
     */
    public User registerAdmin(AdminRegistrationRequest request) {
        // Check if user with this email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User with this email already exists.");
        }

        User newUser = new User();
        newUser.setName(request.getName());
        newUser.setEmail(request.getEmail());
        // Hash the password before saving!
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));

        // Set roles: For admin onboarding, we'll assign the "ADMIN" role.
        Set<String> roles = new HashSet<>();
        roles.add("ADMIN");
        newUser.setRoles(roles);

        // Removed: Logic to link to college. Admin will not be linked to a college at creation.
        // if (request.getCollegeId() != null) {
        //     College college = collegeRepository.findById(request.getCollegeId())
        //             .orElseThrow(() -> new ResourceNotFoundException("College not found with id: " + request.getCollegeId()));
        //     newUser.setCollege(college);
        // }
        newUser.setCollege(null); // Explicitly set college to null for new admins

        // Save the new admin user
        return userRepository.save(newUser);
    }
}
