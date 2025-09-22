package in.krish.repo;

//import in.krish.binding.PostSummaryDto;
import in.krish.entity.Post;
import in.krish.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepo extends JpaRepository<Post, Long> {

    List<Post> findByUser(User user);

}
