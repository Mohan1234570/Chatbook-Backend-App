package in.krish.repo;


import in.krish.entity.Follower;
import in.krish.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FollowerRepo extends JpaRepository<Follower, Long> {

    // Who follows a user (pass the userId of the "following" user)
    List<Follower> findByFollowingUserId(Long userId);

    // Who this user is following (pass the userId of the "follower" user)
    List<Follower> findByFollowerUserId(Long userId);

    boolean existsByFollowerAndFollowing(User follower, User following);

    void deleteByFollowerAndFollowing(User follower, User following);
}


