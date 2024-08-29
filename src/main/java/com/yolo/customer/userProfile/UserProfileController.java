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

    @PreAuthorize("hasAuthority('ROLE_CREATE_PROFILE')")
    @PostMapping("/{username}/profiles")
    public ResponseEntity<String> createUserProfile(
            @PathVariable String username,
            @Valid @RequestBody UserProfileRequestDTO userProfileRequest) {
        UserProfile userProfile = userProfileService.createUserProfile(username, userProfileRequest);
        return ResponseEntity.ok("User profile created successfully.");
    }


    @PreAuthorize("hasAuthority('ROLE_UPDATE_PROFILE')")
    @PatchMapping("/{username}/profiles")
    public ResponseEntity<String> updateUserProfile(
            @PathVariable String username,
            @Valid @RequestBody UpdateUserProfileDTO userProfileUpdateRequest) {
        userProfileService.updateUserProfile(username, userProfileUpdateRequest);
        return ResponseEntity.ok("User profile updated successfully.");
    }

}
