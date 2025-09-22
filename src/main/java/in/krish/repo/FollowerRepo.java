package in.krish.repo;


import in.krish.entity.Follower;
import in.krish.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FollowerRepo extends JpaRepository<Follower, Long> {
    List<User> findFollowersByUserId(@Param("userId") Long userId);
    List<Follower> findByFollowing(User following);
    List<Follower> findByFollower(User follower);
    boolean existsByFollowerAndFollowing(User follower, User following);
    void deleteByFollowerAndFollowing(User follower, User following);
}

