package in.krish.binding;

import in.krish.entity.Comment;

public class CommentResponse {
    private Integer id;
    private String content;
    private String userName; // or userEmail
    private Integer postId;

    public CommentResponse(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.userName = comment.getUser().getEmailid(); // or getEmail()
        this.postId = comment.getPost().getId();
    }

    // getters and setters
}

