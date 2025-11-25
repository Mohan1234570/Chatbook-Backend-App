package in.krish.service;

import in.krish.binding.UserDTO;
import in.krish.entity.User;
import org.springframework.data.domain.Page;


public interface UserService {


	public Page<UserDTO> searchUsers(String query, int page, int size);
	public User getUserByIdInfo(Long userId);
}
