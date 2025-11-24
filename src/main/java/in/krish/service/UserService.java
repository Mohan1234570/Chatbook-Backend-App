package in.krish.service;

import in.krish.binding.UserDTO;
import org.springframework.data.domain.Page;


public interface UserService {


	public Page<UserDTO> searchUsers(String query, int page, int size);

}
