package in.krish.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import in.krish.entity.Comment;

public interface CommentRepo extends JpaRepository<Comment , Integer> {

}
