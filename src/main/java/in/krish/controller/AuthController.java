package in.krish.controller;

import in.krish.binding.ApiResponse;
import in.krish.binding.LoginForm;
import in.krish.binding.RegisterForm;
import in.krish.impl.AuthServiceImpl;
import in.krish.jwtUtils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;
import java.util.Collections;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthServiceImpl blogService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@RequestBody LoginForm login) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(login.getEmail(), login.getPassword()));

        String token = jwtUtil.generateToken(login.getEmail());

        ApiResponse<String> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Login successful",
                token
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@RequestBody RegisterForm form) {
        boolean saved = blogService.registerUser(form);
        if (saved) {
            ApiResponse<String> response = new ApiResponse<>(
                    HttpStatus.OK.value(),
                    "Registered successfully",
                    null
            );
            return ResponseEntity.ok(response);
        } else {
            ApiResponse<String> response = new ApiResponse<>(
                    HttpStatus.BAD_REQUEST.value(),
                    "Duplicate email",
                    null
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}

