package in.krish.controller;

import in.krish.service.FollowerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/follow")
public class FollowerController {

    @Autowired
    private FollowerService followerService;

    @PostMapping("/follow/{targetId}")
    public ResponseEntity<?> followUser(@PathVariable Long targetId,
                                        @RequestHeader("X-USER-ID") Long userId) {
        followerService.follow(userId, targetId);
        return ResponseEntity.ok(Map.of("message", "Followed"));
    }

    @PostMapping("/unfollow/{targetId}")
    public ResponseEntity<?> unfollowUser(@PathVariable Long targetId,
                                          @RequestHeader("X-USER-ID") Long userId) {
        followerService.unfollow(userId, targetId);
        return ResponseEntity.ok(Map.of("message", "Unfollowed"));
    }

    @GetMapping("/status/{targetId}")
    public ResponseEntity<?> checkStatus(@PathVariable Long targetId,
                                         @RequestHeader("X-USER-ID") Long userId) {
        boolean status = followerService.checkFollowStatus(userId, targetId);
        return ResponseEntity.ok(Map.of("isFollowing", status));
    }
}

