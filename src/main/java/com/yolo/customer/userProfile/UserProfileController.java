package com.yolo.customer.userProfile;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserProfileController {
    @Autowired
    private UserProfileService userProfileService;

    @PreAuthorize("hasAuthority('ROLE_CREATE_PROFILE')")
    @PostMapping("/profiles")
    public ResponseEntity<String> createUserProfile(
            @PathVariable String username,
            @Valid @RequestBody UserProfileRequestDTO userProfileRequest) {
        UserProfile userProfile = userProfileService.createUserProfile(userProfileRequest);
        return ResponseEntity.ok("User profile created successfully.");
    }


    @PreAuthorize("hasAuthority('ROLE_UPDATE_PROFILE')")
    @PatchMapping("/profiles")
    public ResponseEntity<String> updateUserProfile(
            @Valid @RequestBody UpdateUserProfileDTO userProfileUpdateRequest) {
        userProfileService.updateUserProfile(userProfileUpdateRequest);
        return ResponseEntity.ok("User profile updated successfully.");
    }

    @PreAuthorize("hasAuthority('ROLE_VIEW_USER_INFO')")
    @GetMapping("/profiles")
    public ResponseEntity<Map<String, Boolean>> updateUserProfile() {
        boolean isUserProfile = userProfileService.checkUserProfile();

        // Create a Map to hold the JSON response
        Map<String, Boolean> response = new HashMap<>();
        response.put("is_user_profile_completed", isUserProfile);

        return ResponseEntity.ok(response);
    }


}
