package com.tomtom.scoop.auth.service;

import com.tomtom.scoop.auth.model.TokenDto;
import com.tomtom.scoop.auth.model.dao.RefreshToken;
import com.tomtom.scoop.auth.repository.RefreshTokenRepository;
import com.tomtom.scoop.auth.util.JwtTokenProvider;
import com.tomtom.scoop.auth.util.JwtTokenResolver;
import com.tomtom.scoop.auth.util.JwtTokenValidator;
import com.tomtom.scoop.domain.user.repository.UserRepository;
import com.tomtom.scoop.global.exception.BusinessException;
import com.tomtom.scoop.global.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;

    private final JwtTokenValidator jwtTokenValidator;

    private final JwtTokenResolver jwtTokenResolver;

    private final RefreshTokenRepository refreshTokenRepository;

    private final UserRepository userRepository;

    public TokenDto reissue(HttpServletRequest request) {
        String token = jwtTokenResolver.resolveToken(request).orElseThrow(() -> new BusinessException(ErrorCode.INVALID_TOKEN));
        String oauthId = jwtTokenValidator.getOauthId(token);

        RefreshToken refreshToken = refreshTokenRepository.findById(oauthId).orElseThrow(() -> new BusinessException(ErrorCode.JWT_REFRESH_TOKEN_EXPIRED));

        if (!jwtTokenValidator.validate(refreshToken.getValue(), oauthId)) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }

        userRepository.findByOauthId(oauthId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        String newAccessToken = jwtTokenProvider.generateAccessToken(oauthId);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(oauthId);

        refreshTokenRepository.save(new RefreshToken(oauthId, newRefreshToken, Duration.ofDays(14).toMillis()));

        return new TokenDto(newAccessToken, newRefreshToken);
    }
}
