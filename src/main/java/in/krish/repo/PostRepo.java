package in.krish.repo;

//import in.krish.binding.PostSummaryDto;
import in.krish.entity.Post;
import in.krish.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepo extends JpaRepository<Post, Integer> {

    List<Post> findByUser(User user);

//    @Query("SELECT new in.krish.binding.PostSummaryDto(" +
//            "p.title, p.content, p.createdAt, COUNT(DISTINCT l), COUNT(DISTINCT c)) " +
//            "FROM Post p " +
//            "LEFT JOIN p.likes l " +
//            "LEFT JOIN p.comments c " +
//            "GROUP BY p.id, p.title, p.content, p.createdAt")
//    List<PostSummaryDto> fetchAllPostSummaries();

}
