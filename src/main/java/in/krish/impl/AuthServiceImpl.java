package in.krish.impl;

import in.krish.binding.LoginForm;
import in.krish.binding.RegisterForm;
import in.krish.entity.User;
import in.krish.entity.User;
import in.krish.repo.UserRepo;
import in.krish.service.AuthService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Override
	public boolean registerUser(RegisterForm form) {
		if (form == null) {
			System.out.println("RegisterForm is null!");
			return false;
		}
		System.out.println("RegisterForm: emailid=" + form.getEmail());

		String email = form.getEmail();
		if (email == null || email.trim().isEmpty()) {
			System.out.println("Email is null or empty");
			return false;
		}

		email = email.trim().toLowerCase();

		User existingUser = userRepo.findByEmailidIgnoreCase(email);
		System.out.println("Existing user for email '" + email + "' is: " + existingUser);

		if (existingUser != null) {
			System.out.println("Duplicate email found!");
			return false;
		}

		User user = new User();
		BeanUtils.copyProperties(form, user);
		user.setEmailid(email);
		// Store the raw password
		user.setRawPassword(form.getPassword());
		user.setPassword(passwordEncoder.encode(form.getPassword()));

		userRepo.save(user);
		System.out.println("User registered successfully");

		return true;
	}





	// ★ FIXED: return User object instead of boolean
	@Override
	public User loginUser(LoginForm form) {
		try {
			Authentication auth = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(
							form.getEmail(),
							form.getPassword()
					)
			);

			// If authentication succeeds, find full user details
			return userRepo.findByEmailid(form.getEmail());

		} catch (AuthenticationException ex) {
			return null;
		}
	}

	@Override
	public User findByEmail(String email) {
		return userRepo.findByEmailid(email);
	}

	@Override
	public User findUserByIdInfo(Long userId) {
		User userOpt = userRepo.findUserByUserId(userId);
		return userOpt;
	}
}
