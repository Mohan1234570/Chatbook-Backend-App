
package in.krish.impl;

import in.krish.binding.UserDTO;
import in.krish.entity.User;
import in.krish.repo.UserRepo;
import in.krish.service.NotificationService;
import in.krish.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.Cacheable;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class UserviceServiceImpl implements UserService {
    @Autowired
    private UserRepo userRepo;

    @Override
    @org.springframework.cache.annotation.Cacheable(value = "userSearchCache", key = "#query + '_' + #page + '_' + #size")
    public Page<UserDTO> searchUsers(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        if (query == null || query.trim().isEmpty()) {
            return Page.empty(pageable); // only return empty if query is blank
        }


        // Call repository method (must return Page<User>)
        Page<User> users = userRepo.searchUsers(query, pageable);

        // Map User to UserDTO
        return users.map(this::convertToDTO);
    }

    @Override
    public User getUserByIdInfo(Long userId) {
        User user = userRepo.findUserByUserId(userId);
        return user;
    }


    private UserDTO convertToDTO(User user) {
        return new UserDTO(
                user.getUserId(),
                user.getFirstname(),
                user.getLastname(),
                user.getEmailid(),
                user.getProfileImageUrl()
        );
    }

}
