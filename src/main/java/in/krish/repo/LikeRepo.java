package in.krish.repo;

import in.krish.entity.Like;
import in.krish.entity.Post;
import in.krish.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepo extends JpaRepository<Like, Integer> {
    Optional<Like> findByPostAndUser(Post post, User user);
}

