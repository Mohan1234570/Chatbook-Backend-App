package in.krish.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import in.krish.entity.Comment;

import java.util.List;

public interface CommentRepo extends JpaRepository<Comment , Integer> {
    List<Comment> findByPostId(Integer postId);
}
