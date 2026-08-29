package org.website.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.website.dto.UserIdentitySummary;
import org.website.dto.UserSummaryDto;
import org.website.service.UserProfileServicePort;

@RestController
@RequestMapping("/internal/users")
public class InternalUserController {

    private final UserProfileServicePort userProfileService;

    public InternalUserController(UserProfileServicePort userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserIdentitySummary> getUserSummary(@PathVariable Long userId) {
        var user = userProfileService.getCurrentUser(userId);
        return ResponseEntity.ok(new UserIdentitySummary(
            user.getUserId(),
            user.getEmail(),
            user.getFullName(),
            user.getRole(),
            true
        ));
    }

    @GetMapping("/{userId}/summary")
    public ResponseEntity<UserSummaryDto> getUserSummaryForBooking(@PathVariable Long userId) {
        var user = userProfileService.getCurrentUser(userId);
        return ResponseEntity.ok(new UserSummaryDto(
            user.getUserId(),
            user.getEmail(),
            user.getFullName(),
            user.getRole()
        ));
    }
}
