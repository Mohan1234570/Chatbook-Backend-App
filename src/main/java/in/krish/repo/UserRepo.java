package in.krish.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import in.krish.entity.User;

public interface UserRepo extends JpaRepository<User, Long> {
	User findByEmailidIgnoreCase(String emailid);
	User findByEmailid(String emailid);
}

