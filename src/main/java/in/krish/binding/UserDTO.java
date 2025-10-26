// UserDTO.java
package in.krish.binding;

import in.krish.entity.User;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class UserDTO {
    private Long id;
    private String firstname;
    private String lastname;
    private String emailid;
    private String profileImageUrl;
    private String bio;

    // ✅ Custom constructor to map from User entity
    public UserDTO(User user) {
        this.id = user.getUserId();
        this.firstname = user.getFirstname();
        this.lastname = user.getLastname();
        this.emailid = user.getEmailid();
        // If you have profile image and bio fields in User entity, map them; otherwise, set null
        this.profileImageUrl = null;
        this.bio = null;
    }
}
