package com.phiny.labs.usermanagement.service;

import com.phiny.labs.common.exception.TokenException;
import com.phiny.labs.usermanagement.entity.RefreshToken;
import com.phiny.labs.usermanagement.entity.User;
import com.phiny.labs.usermanagement.exception.UserNotFoundException;
import com.phiny.labs.usermanagement.repository.RefreshTokenRepository;
import com.phiny.labs.usermanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtGeneratorService jwtGeneratorService;

    @Value("${jwt.refresh.expiration:604800000}") // 7 days default
    private Long refreshTokenExpirationMs;

    public RefreshToken createRefreshToken(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // Delete existing refresh token for this user
        refreshTokenRepository.findByUser(user).ifPresent(refreshTokenRepository::delete);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setExpiresAt(LocalDateTime.now().plusSeconds(refreshTokenExpirationMs / 1000));
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setRevoked(false);

        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(token);
            throw TokenException.expired("Refresh");
        }
        if (Boolean.TRUE.equals(token.getRevoked())) {
            refreshTokenRepository.delete(token);
            throw TokenException.revoked("Refresh");
        }
        return token;
    }

    @Transactional
    public void deleteByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        refreshTokenRepository.deleteByUser(user);
    }

    @Scheduled(fixedRate = 86400000) // Run daily
    public void deleteExpiredTokens() {
        refreshTokenRepository.deleteExpiredTokens(LocalDateTime.now());
    }
}

