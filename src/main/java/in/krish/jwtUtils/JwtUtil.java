package in.krish.jwtUtils;

import in.krish.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret:mySecretKey123}") // fallback to default
    private String SECRET_KEY;

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 day
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    public String extractUsername(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (io.jsonwebtoken.SignatureException e) {
            System.out.println("Invalid JWT signature (dev mode): " + e.getMessage());
            return null; // ignore invalid token during dev
        } catch (Exception e) {
            System.out.println("Invalid JWT (dev mode): " + e.getMessage());
            return null;
        }
    }


    public String extractUsernameFromRequest(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return extractUsername(token);
        }
        return null;
    }

    public boolean validateToken(String token, User userDetails) {
        final String username = extractUsername(token);
        return username != null && username.equals(userDetails.getEmailid()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        try {
            Date expiration = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token)
                    .getBody().getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            return true; // treat invalid token as expired
        }
    }
}

