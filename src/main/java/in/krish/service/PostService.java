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
    Post getPostById(Integer id);
    List<Post> getPostsByUser(String userEmail);
    Post updatePost(Integer id, String title, String content, String userEmail, MultipartFile image) throws IOException;
    void deletePost(Integer id, String userEmail);

    // Like System
    Post likePost(Integer postId, String userEmail);
    Post unlikePost(Integer postId, String userEmail);

    // Comment System
    Comment addComment(Integer postId, String content);
    void deleteComment(Integer postId, Integer commentId, String userEmail);
}