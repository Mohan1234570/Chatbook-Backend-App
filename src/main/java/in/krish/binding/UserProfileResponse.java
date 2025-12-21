package in.krish.binding;

import in.krish.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileResponse {

    private Long userId;
    private String firstname;
    private String lastname;
    private String emailid;

    private long followersCount;
    private long followingCount;

    private List<Post> posts;
}