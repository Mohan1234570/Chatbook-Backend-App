package in.krish.repo;

import in.krish.entity.Follower;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FollowerRepo extends JpaRepository<Follower, Long> {
    // find all users who are following a given user
    List<Follower> findByFollowing_UserId(Long userId);

    // (optional) find all users someone is following
    List<Follower> findByFollower_UserId(Long userId);
}
