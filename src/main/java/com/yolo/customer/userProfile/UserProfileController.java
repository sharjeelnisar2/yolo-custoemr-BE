package com.yolo.customer.userProfile;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserProfileController {
    @Autowired
    private UserProfileService userProfileService;

    @PreAuthorize("hasRole('ROLE_CREATE_PROFILE')")
    @PostMapping("/{username}/profiles")
    public ResponseEntity<String> createUserProfile(
            @PathVariable String username,
            @Valid @RequestBody UserProfileRequestDTO userProfileRequest) {
        UserProfile userProfile = userProfileService.createUserProfile(username, userProfileRequest);
        return ResponseEntity.ok("User profile created successfully.");
    }
}
