package in.krish.service;


import in.krish.binding.LoginForm;
import in.krish.binding.RegisterForm;
import in.krish.entity.User;

public interface AuthService {
    boolean registerUser(RegisterForm form);
    boolean loginUser(LoginForm form);
    User findUserByIdInfo(Long userId);
    User findByEmail(String email);
}

