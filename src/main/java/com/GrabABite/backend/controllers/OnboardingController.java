package com.grababite.backend.controllers;

import com.grababite.backend.dto.OnboardingRequest;
import com.grababite.backend.models.User;
import com.grababite.backend.services.OnboardingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/onboarding")
public class OnboardingController {

    @Autowired
    private OnboardingService onboardingService;

    @PostMapping("/user")
    public ResponseEntity<User> onboardUser(@RequestBody OnboardingRequest request) {
        User onboardedUser = onboardingService.onboardUser(request);
        return ResponseEntity.ok(onboardedUser);
    }
}