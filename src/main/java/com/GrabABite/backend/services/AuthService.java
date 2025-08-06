package com.grababite.backend.services;

import com.grababite.backend.models.User;
import com.grababite.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    /**
     * Authenticates a user with the given email and password.
     * If authentication is successful, it sets the authentication in the SecurityContext
     * and returns the authenticated User object.
     *
     * @param email The user's email (username).
     * @param password The user's password.
     * @return The authenticated User object.
     * @throws UsernameNotFoundException if authentication fails (e.g., bad credentials).
     */
    public User authenticateUser(String email, String password) {
        // Create an authentication token
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password);

        // Authenticate the user
        Authentication authentication = authenticationManager.authenticate(authToken);

        // Set the authenticated object in the SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Retrieve the full User object from the database
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            return userOptional.get();
        } else {
            // This case should ideally not happen if authentication was successful,
            // as userDetailsService would have found the user.
            throw new UsernameNotFoundException("User not found after successful authentication: " + email);
        }
    }
}
