package in.krish.binding;

import in.krish.entity.User;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class UserDTO {
    private Long userId;
    private String firstname;
    private String lastname;
    private String emailid;
    private String profileImageUrl;

    public UserDTO(User user) {
        if (user != null) {
            this.userId = user.getUserId();
            this.firstname = user.getFirstname();
            this.lastname = user.getLastname();
            this.emailid = user.getEmailid();
            this.profileImageUrl = user.getProfileImageUrl();
        }
    }
}
