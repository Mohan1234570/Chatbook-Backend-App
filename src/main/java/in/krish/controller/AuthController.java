//package in.krish.controller;
//
//import in.krish.binding.ApiResponse;
//import in.krish.binding.LoginForm;
//import in.krish.binding.RegisterForm;
//import in.krish.impl.AuthServiceImpl;
//import in.krish.jwtUtils.JwtUtil;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.web.bind.annotation.*;
//
//import javax.naming.AuthenticationException;
//import java.util.Collections;
//
//@RestController
//@RequestMapping("/api/users")
//@CrossOrigin(origins = "http://localhost:3000")
//public class AuthController {
//
//    @Autowired
//    private AuthenticationManager authManager;
//
//    @Autowired
//    private JwtUtil jwtUtil;
//
//    @Autowired
//    private AuthServiceImpl blogService;
//
//    @PostMapping("/login")
//    public ResponseEntity<ApiResponse<String>> login(@RequestBody LoginForm login) {
//        authManager.authenticate(
//                new UsernamePasswordAuthenticationToken(login.getEmail(), login.getPassword()));
//
//        String token = jwtUtil.generateToken(login.getEmail());
//
//        ApiResponse<String> response = new ApiResponse<>(
//                HttpStatus.OK.value(),
//                "Login successful",
//                token
//        );
//        return ResponseEntity.ok(response);
//    }
//
//    @PostMapping("/register")
//    public ResponseEntity<ApiResponse<String>> register(@RequestBody RegisterForm form) {
//        boolean saved = blogService.registerUser(form);
//        if (saved) {
//            ApiResponse<String> response = new ApiResponse<>(
//                    HttpStatus.OK.value(),
//                    "Registered successfully",
//                    null
//            );
//            return ResponseEntity.ok(response);
//        } else {
//            ApiResponse<String> response = new ApiResponse<>(
//                    HttpStatus.BAD_REQUEST.value(),
//                    "Duplicate email",
//                    null
//            );
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//        }
//    }
//}
//


package in.krish.controller;

import in.krish.binding.ApiResponse;
import in.krish.binding.LoginForm;
import in.krish.binding.RegisterForm;
import in.krish.entity.User;
import in.krish.impl.AuthServiceImpl;
import in.krish.jwtUtils.JwtUtil;
import in.krish.impl.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class AuthController {

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthServiceImpl authService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@RequestBody LoginForm login, HttpServletResponse response) {
        System.out.println("==============================");
        System.out.println("🔹 [LOGIN API CALLED]");
        System.out.println("Email: " + login.getEmail());
        System.out.println("Client ID: " + login.getClientId());
        System.out.println("IP: " + login.getIp());
        System.out.println("==============================");

        try {
            // ✅ 1. Authenticate credentials
            System.out.println("🔹 Authenticating user...");
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(login.getEmail(), login.getPassword()));
            System.out.println("✅ Authentication successful.");
        } catch (Exception ex) {
            System.out.println("❌ Authentication failed: " + ex.getMessage());
            ApiResponse<String> r = new ApiResponse<>(HttpStatus.UNAUTHORIZED.value(), "Invalid credentials", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(r);
        }

        // ✅ 2. Find user by email
        System.out.println("🔹 Fetching user from DB...");
        User user = authService.findByEmail(login.getEmail());
        if (user == null) {
            System.out.println("❌ User not found in DB for email: " + login.getEmail());
            ApiResponse<String> r = new ApiResponse<>(HttpStatus.UNAUTHORIZED.value(), "Invalid user", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(r);
        }

        // ✅ 3. Extract roles
        var roles = user.getRoles() == null ? java.util.List.of() :
                user.getRoles().stream().map(r -> r.getName()).collect(Collectors.toList());
        System.out.println("🔹 User roles: " + roles);

        // ✅ 4. Generate JWT
        System.out.println("🔹 Generating access token...");
        String accessToken = jwtUtil.generateToken(user);
        System.out.println("✅ Access token generated: " + accessToken);

        // ✅ 5. Generate Refresh Token
        System.out.println("🔹 Generating refresh token...");
        String refreshToken = refreshTokenService.createRefreshToken(user.getUserId(), login.getClientId(), login.getIp());
        System.out.println("✅ Refresh token generated: " + refreshToken);

        // ✅ 6. Set refresh token in cookie
        System.out.println("🔹 Setting refresh token cookie...");
        ResponseCookie cookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(false) // ⚠️ Set true in production
                .path("/api/users")
                .maxAge(Duration.ofDays(30))
                .sameSite("Strict")
                .build();
        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        // ✅ 7. Send success response
        ApiResponse<String> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), "Login successful", accessToken);
        System.out.println("✅ Login process complete — sending response.");
        System.out.println("==============================");
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<String>> refresh(@CookieValue(name = "refresh_token", required = false) String refreshToken,
                                                       HttpServletResponse response) {
        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(HttpStatus.UNAUTHORIZED.value(), "No refresh token", null));
        }

        var opt = refreshTokenService.validate(refreshToken);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(HttpStatus.UNAUTHORIZED.value(), "Invalid/expired refresh token", null));
        }

        var rt = opt.get();
        // load user
        User user = authService.findUserByIdInfo(rt.getUserId());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(HttpStatus.UNAUTHORIZED.value(), "User not found", null));
        }

        var roles = user.getRoles() == null ? java.util.List.of() :
                user.getRoles().stream().map(r -> r.getName()).collect(Collectors.toList());

        String newAccessToken = jwtUtil.generateToken(user);
        String newRefresh = refreshTokenService.rotate(refreshToken);

        ResponseCookie cookie = ResponseCookie.from("refresh_token", newRefresh)
                .httpOnly(true)
                .secure(false)
                .path("/api/users")
                .maxAge(Duration.ofDays(30))
                .sameSite("Strict")
                .build();
        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Refreshed", newAccessToken));
    }


    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@RequestBody RegisterForm form) {
        boolean saved = authService.registerUser(form);
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

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@CookieValue(name = "refresh_token", required = false) String refreshToken,
                                                    @RequestParam(required = false) String userEmail) {
        if (refreshToken != null) refreshTokenService.revoke(refreshToken);
        if (userEmail != null) {
            User user = authService.findByEmail(userEmail);
            if (user != null) refreshTokenService.revokeAllForUser(user.getUserId());
        }
        ApiResponse<Void> resp = new ApiResponse<>(HttpStatus.OK.value(), "Logged out", null);
        return ResponseEntity.ok(resp);
    }
}
