package in.krish.service;


import in.krish.binding.LoginForm;
import in.krish.binding.RegisterForm;

public interface AuthService {
    boolean registerUser(RegisterForm form);
    boolean loginUser(LoginForm form);
}

