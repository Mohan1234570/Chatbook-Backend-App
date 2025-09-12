package in.krish.service;

import java.util.List;
import in.krish.binding.CreatePost;
import in.krish.binding.LoginForm;
import in.krish.binding.RegisterForm;
import in.krish.entity.Post;

public interface UserService {

	public String login(LoginForm form);

	public boolean registrationForm(RegisterForm form);

	public boolean createPost(CreatePost form);

	public List<Post> getDashboardData(Integer userId, Post post);


}
