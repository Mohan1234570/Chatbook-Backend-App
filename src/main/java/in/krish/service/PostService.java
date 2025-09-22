//package in.krish.service;
//
//import in.krish.binding.PostRequest;
//import in.krish.entity.Post;
//
//public interface PostService {
//
//    public Post createPost(PostRequest request, String userEmail);
//}


package in.krish.service;

import in.krish.binding.CommentDTO;
import in.krish.binding.PostRequest;
//import in.krish.binding.PostSummaryDto;
import in.krish.entity.Post;
import in.krish.entity.Comment;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

public interface PostService {
    // Post Management
    Post createPost(PostRequest request, String userEmail, MultipartFile image) throws IOException;
//    public List<PostSummaryDto> fetchAllPostSummaries();
    Post getPostById(Long id);
    public List<Post> getAllPosts();
    List<Post> getPostsByUser(String userEmail);
    Post updatePost(Long id, String title, String content, String userEmail, MultipartFile image) throws IOException;
    void deletePost(Long id, String userEmail);

    // Like System
    Post likePost(Long postId, String userEmail);
    Post unlikePost(Long postId, String userEmail);

    // Comment System
    Comment addComment(Long postId, String content);
    void deleteComment(Long postId, Long commentId, String userEmail);
    public List<Comment> getAllCommentsForPost(Long postId);
}