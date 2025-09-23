package in.krish.binding;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class PostDTO {
    private Long id;
    private String title;
    private String content;
    private String imageUrl;
    private LocalDateTime dateCreated;
    private int likesCount;
    private List<String> likedBy;

    public PostDTO(in.krish.entity.Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.imageUrl = post.getImageUrl();
        this.dateCreated = post.getDateCreated();
        this.likesCount = post.getLikesCount();
        this.likedBy = post.getLikedBy();
    }
}
