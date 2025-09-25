package in.krish.service;

import in.krish.entity.User;

public interface FollowerService {
    String followUser(Long followingId, String followerEmail);
    String unfollowUser(Long followingId, String followerEmail);
}

