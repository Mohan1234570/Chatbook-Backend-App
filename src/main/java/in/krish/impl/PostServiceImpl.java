//package in.krish.impl;
//
//import in.krish.binding.PostRequest;
//import in.krish.entity.Post;
//import in.krish.entity.User;
//import in.krish.repo.PostRepo;
//import in.krish.repo.UserRepo;
//import in.krish.service.PostService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//@Service
//public class PostServiceImpl implements PostService {
//
//    @Autowired
//    private UserRepo userRepo;
//
//    @Autowired
//    private PostRepo postRepo;
//
//    public Post createPost(PostRequest request, String userEmail) {
//        User user = userRepo.findByEmailid(userEmail);
//        if (user == null) {
//            throw new RuntimeException("User not found with email: " + userEmail);
//        }
//
//        Post post = new Post();
//        post.setTitle(request.getTitle());
//        post.setContent(request.getContent());
//        post.setUser(user);
//        return postRepo.save(post);
//    }
//
//}


package in.krish.impl;

import in.krish.binding.PostRequest;
//import in.krish.binding.PostSummaryDto;
import in.krish.entity.Like;
import in.krish.entity.Post;
import in.krish.entity.User;
import in.krish.entity.Comment;
import in.krish.repo.LikeRepo;
import in.krish.repo.PostRepo;
import in.krish.repo.UserRepo;
import in.krish.repo.CommentRepo;
import in.krish.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PostRepo postRepo;

    @Autowired
    private LikeRepo likeRepo;

    @Autowired
    private CommentRepo commentRepo;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;
    @Override
    public Post createPost(PostRequest request, String userEmail, MultipartFile image) throws IOException {
        User user = userRepo.findByEmailid(userEmail);
        if (user == null) {
            throw new RuntimeException("User not found with email: " + userEmail);
        }

        Post post = new Post();
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setUser(user);

        if (image != null && !image.isEmpty()) {
            // Ensure upload directory exists
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Create unique filename
            String filename = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
            Path filePath = uploadPath.resolve(filename);

            // Save the image file with try-with-resources for stream handling
            try (InputStream inputStream = image.getInputStream()) {
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new IOException("Failed to save image file: " + filename, e);
            }

            // Set image URL/path in Post entity
            post.setImageUrl("/uploads/" + filename);
        }

        return postRepo.save(post);
    }

//
//    @Override
//    public List<PostSummaryDto> fetchAllPostSummaries() {
//        return postRepo.fetchAllPostSummaries();
//    }



    @Override
    public Post getPostById(Integer id) {
        return postRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));
    }

    @Transactional
    @Override
    public List<Post> getPostsByUser(String userEmail) {
        User user = userRepo.findByEmailid(userEmail);
        if (user == null) {
            throw new RuntimeException("User not found with email: " + userEmail);
        }
        return postRepo.findByUser(user);
    }

    @Override
    public Post updatePost(Integer id, String title, String content, String userEmail, MultipartFile image) throws IOException {
        Post post = postRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));

        User user = userRepo.findByEmailid(userEmail);
        if (user == null || !post.getUser().equals(user)) {
            throw new RuntimeException("Unauthorized to update this post");
        }

        post.setTitle(title);
        post.setContent(content);

        if (image != null && !image.isEmpty()) {
            // Delete old image if exists
            if (post.getImageUrl() != null) {
                Path oldImagePath = Paths.get(uploadDir, post.getImageUrl().replace("/uploads/", ""));
                Files.deleteIfExists(oldImagePath);
            }

            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate unique filename
            String filename = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
            Path filePath = uploadPath.resolve(filename);

            // Save the file
            Files.copy(image.getInputStream(), filePath);

            // Set the image URL
            post.setImageUrl("/uploads/" + filename);
        }

        return postRepo.save(post);
    }
    @Override
    public void deletePost(Integer id, String userEmail) {
        Post post = postRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));

        User user = userRepo.findByEmailid(userEmail);
        if (user == null || !post.getUser().equals(user)) {
            throw new RuntimeException("Unauthorized to delete this post");
        }

        // Delete image if exists
        if (post.getImageUrl() != null) {
            try {
                Path imagePath = Paths.get(uploadDir, post.getImageUrl().replace("/uploads/", ""));
                Files.deleteIfExists(imagePath);
            } catch (IOException e) {
                // Log the error or throw a RuntimeException as needed
                throw new RuntimeException("Failed to delete image file", e);
            }
        }

        postRepo.delete(post);
    }


    @Override
    @Transactional
    public Post likePost(Integer postId, String userEmail) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));

        User user = userRepo.findByEmailid(userEmail);
        if (user == null) {
            throw new RuntimeException("User not found with email: " + userEmail);
        }

        // Check if already liked
        boolean alreadyLiked = post.getLikes().stream()
                .anyMatch(like -> like.getUser().getUserId().equals(user.getUserId()));

        if (alreadyLiked) {
            throw new RuntimeException("User already liked this post");
        }

        Like like = new Like();
        like.setUser(user);
        like.setPost(post);

        likeRepo.save(like);
        post.addLike(like);

        return postRepo.save(post);
    }


    @Override
    @Transactional
    public Post unlikePost(Integer postId, String userEmail) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));

        User user = userRepo.findByEmailid(userEmail);
        if (user == null) {
            throw new RuntimeException("User not found with email: " + userEmail);
        }

        // Find the Like entity to remove
        Like likeToRemove = null;
        for (Like like : post.getLikes()) {
            if (like.getUser().getUserId().equals(user.getUserId())) {
                likeToRemove = like;
                break;
            }
        }

        if (likeToRemove == null) {
            throw new RuntimeException("User has not liked this post");
        }

        post.getLikes().remove(likeToRemove);
        likeRepo.delete(likeToRemove); // assuming you have likeRepo

        return postRepo.save(post); // like count is derived from likedBy.size()
    }


    @Override
    public Comment addComment(Integer postId, String content, String userEmail) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));

        User user = userRepo.findByEmailid(userEmail);
        if (user == null) {
            throw new RuntimeException("User not found with email: " + userEmail);
        }

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setPost(post);
        comment.setUser(user);

        return commentRepo.save(comment);
    }

    @Override
    public void deleteComment(Integer postId, Integer commentId, String userEmail) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));

        Comment comment = commentRepo.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + commentId));

        User user = userRepo.findByEmailid(userEmail);
        if (user == null) {
            throw new RuntimeException("User not found with email: " + userEmail);
        }

        // Check if user is authorized to delete the comment
        if (!comment.getUser().equals(user) && !post.getUser().equals(user)) {
            throw new RuntimeException("Unauthorized to delete this comment");
        }

        commentRepo.delete(comment);
    }
}