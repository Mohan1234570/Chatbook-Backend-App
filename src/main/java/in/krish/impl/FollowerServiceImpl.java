package in.krish.impl;

import in.krish.entity.Follower;
import in.krish.entity.User;
import in.krish.repo.FollowerRepo;
import in.krish.repo.UserRepo;
import in.krish.service.FollowerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FollowerServiceImpl implements FollowerService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private FollowerRepo followerRepo;

    @Override
    public String followUser(Long followingId, String followerEmail) {
        User follower = userRepo.findByEmailid(followerEmail);
        User following = userRepo.findById(followingId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (follower.equals(following)) {
            return "Cannot follow yourself";
        }

        boolean exists = followerRepo.existsByFollowerAndFollowing(follower, following);
        if (exists) return "Already following";

        Follower relation = new Follower(follower, following);
        followerRepo.save(relation);

        return follower.getFirstname() + " is now following " + following.getFirstname();
    }

    @Override
    public String unfollowUser(Long followingId, String followerEmail) {
        User follower = userRepo.findByEmailid(followerEmail);

        Follower relation = followerRepo.findByFollower_UserIdAndFollowing_UserId(
                follower.getUserId(), followingId);

        if (relation != null) {
            followerRepo.delete(relation);
            return "Unfollowed successfully";
        }
        return "Not following";
    }
}
