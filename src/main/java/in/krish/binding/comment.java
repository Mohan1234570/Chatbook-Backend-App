package in.krish.binding;

import com.fasterxml.jackson.annotation.JsonBackReference;
import in.krish.entity.Post;
import lombok.*;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class comment {
	
	private String name;
	
	private String email;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "post_id")
	@JsonBackReference
	private Post post;


}
