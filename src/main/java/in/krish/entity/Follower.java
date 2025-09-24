package in.krish.entity;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Setter
@Getter
@Table(name = "followers",
        uniqueConstraints = @UniqueConstraint(columnNames = {"follower_id", "following_id"}))
public class Follower {

    // Getters and Setters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The user who is following
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id", nullable = false)
    private User follower;

    // The user being followed
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "following_id", nullable = false)
    private User following;

    public Follower() {}

    public Follower(User follower, User following) {
        this.follower = follower;
        this.following = following;
    }

    public void setFollower(User follower) { this.follower = follower; }

    public void setFollowing(User following) { this.following = following; }
}

