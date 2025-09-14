package in.krish.entity;

import java.time.LocalDate;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "comment")
public class Comment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)

	private Integer id ;
	@Lob
	private String content ;
	private String name ;
	private String email ;
	
	@CreationTimestamp
	private LocalDate createdon ;


	@ManyToOne
	@JoinColumn(name = "post_id", nullable = false)
	private Post post;


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;
	
	
	
}
