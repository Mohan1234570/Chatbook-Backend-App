
package in.krish.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Getter
@Setter
@Table(name = "post")
public class Post {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false)
	private String title;

	@Lob
	@Basic(fetch = FetchType.LAZY)
	private String content;

	@Column(name = "image_url")
	private String imageUrl;

	@CreationTimestamp
	@Column(name = "date_created", updatable = false)
	private LocalDateTime dateCreated;

	@CreationTimestamp
	private LocalDateTime createdAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	@JsonIgnore
	private User user;

	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JsonManagedReference
	private List<Comment> comments = new ArrayList<>();

	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	private List<Like> likes = new ArrayList<>();


	// Convenience field: total likes
	@Transient
	private int likesCount;

	public int getLikesCount() {
		return likes.size();
	}

	// Convenience field: users who liked the post
	@Transient
	private List<String> likedBy;

	public List<String> getLikedBy() {
		return likes.stream()
				.map(like -> like.getUser().getEmailid()) // or other identifier
				.collect(Collectors.toList());
	}

	// Helper methods for managing relationships
	public void addComment(Comment comment) {
		comments.add(comment);
		comment.setPost(this);
	}

	public void removeComment(Comment comment) {
		comments.remove(comment);
		comment.setPost(null);
	}

	public void addLike(Like like) {
		likes.add(like);
		like.setPost(this);
	}

	public void removeLike(Like like) {
		likes.remove(like);
		like.setPost(null);
	}
}
