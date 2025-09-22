package in.krish.entity;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Setter
@Getter
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Who receives the notification
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Who triggered the notification
    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    // Optional: linked post
    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    private String message;

    private Boolean isRead = false;

    private LocalDateTime createdAt = LocalDateTime.now();

    // getters & setters
}
