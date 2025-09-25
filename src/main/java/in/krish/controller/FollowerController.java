package in.krish.controller;

import in.krish.service.FollowerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/follow")
public class FollowerController {

    @Autowired
    private FollowerService followerService;

    // Follow a user
    @PostMapping("/{followingId}")
    public String followUser(@PathVariable Long followingId, Principal principal) {
        String email = principal.getName();
        return followerService.followUser(followingId, email);
    }

    // Unfollow a user
    @DeleteMapping("/{followingId}")
    public String unfollowUser(@PathVariable Long followingId, Principal principal) {
        String email = principal.getName();
        return followerService.unfollowUser(followingId, email);
    }
}

