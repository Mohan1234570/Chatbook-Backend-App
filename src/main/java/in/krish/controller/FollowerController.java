package in.krish.controller;

import in.krish.service.FollowerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;
@RestController
@RequestMapping("/api/follow")
public class FollowerController {

    @Autowired
    private FollowerService followerService;

    // Follow a user
    @PostMapping("/follow/{targetId}")
    public ResponseEntity<?> followUser(
            @PathVariable Long targetId,
            @AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername(); // get logged-in user from JWT
        followerService.followByEmail(email, targetId);

        return ResponseEntity.ok(Map.of("message", "Followed"));
    }

    // Unfollow a user
    @PostMapping("/unfollow/{targetId}")
    public ResponseEntity<?> unfollowUser(
            @PathVariable Long targetId,
            @AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername();
        followerService.unfollowByEmail(email, targetId);

        return ResponseEntity.ok(Map.of("message", "Unfollowed"));
    }

    // Check follow status
    @GetMapping("/status/{targetId}")
    public ResponseEntity<?> checkStatus(
            @PathVariable Long targetId,
            @AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername();
        boolean status = followerService.checkFollowStatusByEmail(email, targetId);

        return ResponseEntity.ok(Map.of("isFollowing", status));
    }
}



