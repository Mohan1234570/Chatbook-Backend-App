//package in.krish.controller;
//
//import java.util.List;
//import java.util.Optional;
//
//import javax.servlet.http.HttpSession;
//
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import in.krish.binding.CreatePost;
//import in.krish.binding.LoginForm;
//import in.krish.binding.RegisterForm;
//import in.krish.entity.Post;
//import in.krish.repo.PostRepo;
//import in.krish.service.UserService;
//
//@Controller
//public class UserController {
//
//	private final PostRepo repo;
//	private final UserService userService;
//
//	// Constructor Injection
//	public UserController(PostRepo repo, UserService userService) {
//		this.repo = repo;
//		this.userService = userService;
//	}
//
//	@GetMapping("/login")
//	public String loginForm(Model model) {
//		model.addAttribute("logindata", new LoginForm());
//		return "login";
//	}
//
//	@PostMapping("/login")
//	public String loadLogin(@ModelAttribute("logindata") LoginForm form, Model model) {
//		String status = userService.login(form);
//		if ("Success".equals(status)) {
//			return "redirect:/dashboard";
//		}
//		model.addAttribute("errmsg", status);
//		return "login";
//	}
//
//	@GetMapping("/dashboard")
//	public String loadDashboard(Model model, Post post, HttpSession session) {
//		Integer userId = (Integer) session.getAttribute("userId");
//		List<Post> data = userService.getDashboardData(userId, post);
//		model.addAttribute("allPosts", data);
//		return "dashboard";
//	}
//
//	@GetMapping("/register")
//	public String registrationForm(Model model) {
//		model.addAttribute("registrationForm", new RegisterForm());
//		return "registration";
//	}
//
//	@PostMapping("/register")
//	public String showBlogUserForm(@ModelAttribute("registrationForm") RegisterForm form, Model model) {
//		boolean users = userService.registrationForm(form);
//		if (users) {
//			model.addAttribute("smsg", "user saved....");
//		} else {
//			model.addAttribute("emsg", "email already exist");
//		}
//		return "registration";
//	}
//
//	@GetMapping("/logout")
//	public String logout() {
//		return "index";
//	}
//
//	@GetMapping("/createPost")
//	public String createPost(Model model) {
//		model.addAttribute("createPost", new CreatePost());
//		return "createPost";
//	}
//
//	@PostMapping("/create")
//	public String createPosthandle(@ModelAttribute("createPost") CreatePost form, Model model) {
//		boolean status = userService.createPost(form);
//
//		if (status) {
//			model.addAttribute("succMsg", "Post created");
//		} else {
//			model.addAttribute("errMsg", "post not created");
//		}
//		return "createPost";
//	}
//
//	@GetMapping("/edit")
//	public String editPost(@RequestParam("id") Integer userId, Model model) {
//		Optional<Post> findById = repo.findById(userId);
//		findById.ifPresent(post -> model.addAttribute("createPost", post));
//		return "createPost";
//	}
//
//}
