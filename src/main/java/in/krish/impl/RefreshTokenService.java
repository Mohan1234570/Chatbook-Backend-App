package in.krish.impl;

import in.krish.entity.RefreshToken;
import in.krish.repo.RefreshTokenRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

@Service
public class RefreshTokenService {

    @Autowired
    private RefreshTokenRepo refreshTokenRepo;

    private final SecureRandom secureRandom = new SecureRandom();

    public String createRefreshToken(Long userId, String clientId, String ipAddress) {
        byte[] random = new byte[48];
        secureRandom.nextBytes(random);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(random);

        RefreshToken rt = new RefreshToken();
        rt.setToken(token);
        rt.setUserId(userId);
        rt.setIssuedAt(Instant.now());
        rt.setExpiresAt(Instant.now().plusSeconds(60L * 60 * 24 * 30)); // 30 days
        rt.setClientId(clientId);
        rt.setIpAddress(ipAddress);
        rt.setRevoked(false);

        refreshTokenRepo.save(rt);
        return token;
    }

    public Optional<RefreshToken> validate(String token) {
        return refreshTokenRepo.findByToken(token)
                .filter(rt -> !rt.isRevoked())
                .filter(rt -> rt.getExpiresAt().isAfter(Instant.now()));
    }

    public void revoke(String token) {
        refreshTokenRepo.findByToken(token).ifPresent(rt -> {
            rt.setRevoked(true);
            refreshTokenRepo.save(rt);
        });
    }

    public String rotate(String oldToken) {
        Optional<RefreshToken> opt = refreshTokenRepo.findByToken(oldToken);
        if (opt.isEmpty()) throw new RuntimeException("Invalid refresh token");

        RefreshToken rt = opt.get();
        rt.setRevoked(true);
        refreshTokenRepo.save(rt);

        return createRefreshToken(rt.getUserId(), rt.getClientId(), rt.getIpAddress());
    }

    public void revokeAllForUser(Long userId) {
        refreshTokenRepo.deleteByUserId(userId);
    }
}
