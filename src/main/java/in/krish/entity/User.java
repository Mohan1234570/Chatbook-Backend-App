package in.krish.entity;

import java.util.*;
import java.util.stream.Collectors;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Entity
@Table(name = "Users")
@Setter
@Getter
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long userId;

	private String firstname;
	private String lastname;

	@Column(unique = true, nullable = false)
	private String emailid;
	private String password;

	@Column(name = "raw_password") // This will store the actual (plain text) password
	private String rawPassword;

	// Store authorities as Strings in DB
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "authorities", joinColumns = @JoinColumn(name = "user_id"))
	@Column(name = "authority")
	private Set<String> authorities = new HashSet<>();

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "user", fetch = FetchType.LAZY)
	@JsonIgnore
	private List<Post> posts;


	// Expose authorities as GrantedAuthority collection
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities.stream()
				.map(SimpleGrantedAuthority::new)
				.collect(Collectors.toSet());
	}

	// Accept a collection of GrantedAuthority and store as strings
	public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
		this.authorities = authorities.stream()
				.map(GrantedAuthority::getAuthority)
				.collect(Collectors.toSet());
	}

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	private List<Like> likes = new ArrayList<>();

}
