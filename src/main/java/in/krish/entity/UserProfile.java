package in.krish.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "user_profiles")
@Getter
@Setter
public class UserProfile {

    @Id
    private Long userId;

    private String email;
    private String fullName;
    private Long tenantId;
    private String plan;
    private String status;
}

