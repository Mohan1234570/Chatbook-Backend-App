

package in.krish.impl;

import in.krish.binding.PostRequest;
import in.krish.entity.*;
import in.krish.repo.*;
import in.krish.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;


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

    @Autowired
    private FeedRepository feedRepo;

    @Autowired
    private FollowerRepo followerRepo;

    @Autowired
    private NotificationRepo notificationRepo;

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
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String filename = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
            Path filePath = uploadPath.resolve(filename);

            try (InputStream inputStream = image.getInputStream()) {
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new IOException("Failed to save image file: " + filename, e);
            }

            post.setImageUrl("/uploads/" + filename);

            Post savedPost = postRepo.save(post);

            // fan-out notifications
// ✅ fan-out notifications to followers
            Long userId = user.getUserId();
            List<Follower> followers = followerRepo.findByFollowingUserId(userId);
            for (Follower f : followers) {
                User follower = f.getFollower();
                FeedEntry entry = new FeedEntry();
                entry.setUser(follower);
                entry.setPost(savedPost);
                feedRepo.save(entry);

                Notification notif = new Notification();
                notif.setUser(follower);
                notif.setSender(user);
                notif.setPost(savedPost);
                notif.setMessage(user.getFirstname() + " created a new post");
                notificationRepo.save(notif);
            }

            return savedPost;   // ✅ return savedPost instead of post
        }

        return postRepo.save(post);  // ✅ fallback when no image
    }   // <<< THIS closing brace was missing in your code!


    @Override
        @Transactional(readOnly = true)
        public List<Post> getAllPosts () {
            return postRepo.findAll();
        }


        @Override
        public Post getPostById (Long id){
            return postRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));
        }

        @Transactional
        @Override
        public List<Post> getPostsByUser (String userEmail){
            User user = userRepo.findByEmailid(userEmail);
            if (user == null) {
                throw new RuntimeException("User not found with email: " + userEmail);
            }
            return postRepo.findByUser(user);
        }

        @Override
        public Post updatePost (Long id, String title, String content, String userEmail, MultipartFile image) throws
        IOException {
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
        public void deletePost(Long id, String userEmail){
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
        public Post likePost (Long postId, String userEmail){
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
        public Post unlikePost (Long postId, String userEmail){
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
        @Transactional
        public Comment addComment (Long postId, String content){
            Post post = postRepo.findById(postId)
                    .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));

            // Get the currently authenticated user from the Security Context
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                throw new RuntimeException("User not authenticated");
            }

            String email = authentication.getName(); // Assuming username is email
            User user = userRepo.findByEmailid(email);
            if (user == null) {
                throw new RuntimeException("User not found with email: " + email);
            }

            Comment comment = new Comment();
            comment.setContent(content);
            comment.setPost(post);
            comment.setUser(user);

            return commentRepo.save(comment);
        }


        @Override
        public void deleteComment (Long postId, Long commentId, String userEmail){
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
        @Override
        @Transactional(readOnly = true)
        public List<Comment> getAllCommentsForPost (Long postId){
            return commentRepo.findByPostId(postId);
        }

    }

