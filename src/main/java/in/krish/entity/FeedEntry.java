package in.krish.entity;


import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "feed_entries")
public class FeedEntry {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    private LocalDateTime createdAt = LocalDateTime.now();

}

