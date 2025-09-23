
package in.krish.controller;

import in.krish.binding.*;
//import in.krish.binding.PostSummaryDto;
import in.krish.entity.FeedEntry;
import in.krish.entity.Post;
import in.krish.entity.Comment;
import in.krish.impl.PostServiceImpl;
import in.krish.repo.FeedRepository;
import in.krish.repo.PostRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class PostsController {

    @Autowired
    private PostServiceImpl postService;

    @Autowired
    private FeedRepository feedRepo;


    //this method for create a new post
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> createPost(
            @ModelAttribute PostRequest request,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam("userEmail") String userEmail) {
        try {
            Post post = postService.createPost(request, userEmail, image);
            return ResponseEntity.ok(post);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping
    public ApiResponse<List<PostDTO>> getAllPosts() {
        List<Post> posts = postService.getAllPosts();

        List<PostDTO> postDTOs = posts.stream()
                .map(PostDTO::new)
                .collect(Collectors.toList());

        return new ApiResponse<>(200, "Posts fetched successfully", postDTOs);
    }



    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Post>> getPostById(@PathVariable Long id) {
        Post post = postService.getPostById(id);
        return ResponseEntity.ok(new ApiResponse<>(200, "Post fetched successfully", post));
    }


    @GetMapping("/userPosts")
    public ApiResponse<List<PostDTO>> getMyPosts(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername(); // email from token
        List<Post> posts = postService.getPostsByUser(email);

        // Convert to DTO
        List<PostDTO> postDTOs = posts.stream()
                .map(PostDTO::new)
                .collect(Collectors.toList());

        return new ApiResponse<>(200, "Posts fetched successfully", postDTOs);
    }

    @PutMapping(value = "/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updatePost(
            @PathVariable Long postId,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam("userEmail") String userEmail) {
        try {
            Post updatedPost = postService.updatePost(postId, title, content, userEmail, image);
            ApiResponse<Post> response = new ApiResponse<>(
                    HttpStatus.OK.value(),
                    "Post updated successfully",
                    updatedPost
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id, @RequestParam String userEmail) {
        try {
            postService.deletePost(id, userEmail);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<?> likePost(@PathVariable Long postId, @RequestParam String userEmail) {
        try {
            Post post = postService.likePost(postId, userEmail);
            return ResponseEntity.ok(post);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{postId}/unlike")
    public ResponseEntity<?> unlikePost(@PathVariable Long postId, @RequestParam String userEmail) {
        try {
            Post post = postService.unlikePost(postId, userEmail);
            return ResponseEntity.ok(post);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{postId}/comment")
    public ResponseEntity<?> addComment(@PathVariable Long postId, @RequestBody CommentRequest request) {
        try {
            Comment comment = postService.addComment(postId, request.getContent());
            CommentResponse response = new CommentResponse(comment.getId(), comment.getContent(), comment.getUser().getEmailid());
            return ResponseEntity.ok(new CommentResponse(comment));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @DeleteMapping("/{postId}/comment/{commentId}")
    public ResponseEntity<?> deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @RequestParam String userEmail) {
        try {
            postService.deleteComment(postId, commentId, userEmail);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Get all comments for a specific post
    @GetMapping("/{postId}/comments")
    public ApiResponse<List<CommentDTO>> getAllComments(@PathVariable Long postId) {
        List<Comment> comments = postService.getAllCommentsForPost(postId);

        // Convert Comment entities to CommentDTO
        List<CommentDTO> commentDTOs = comments.stream()
                .map(CommentDTO::new)
                .collect(Collectors.toList());

        return new ApiResponse<>(200, "Comments fetched successfully", commentDTOs);
    }
    @GetMapping("/feeds")
    public ResponseEntity<List<FeedEntry>> getFeed(Principal principal) {
        Long userId = getUserIdFromPrincipal(principal);
        return ResponseEntity.ok(feedRepo.findByUserUserIdOrderByCreatedAtDesc(userId));
    }

    private Long getUserIdFromPrincipal(Principal principal) {
        return 1L; // later you’ll map Principal -> actual User ID
    }

}