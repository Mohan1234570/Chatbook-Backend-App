package in.krish.binding;

import in.krish.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class CommentDTO {
    private Long id;
    private String content;
    private String name;
    private String email;
    private LocalDate createdOn;

    public CommentDTO(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.name = comment.getName();
        this.email = comment.getEmail();
        this.createdOn = comment.getCreatedon();
    }

    public CommentDTO(CommentDTO commentDTO) {
    }
}

