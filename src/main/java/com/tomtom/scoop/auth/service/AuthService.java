package com.tomtom.scoop.auth.service;

import com.tomtom.scoop.auth.model.TokenDto;
import com.tomtom.scoop.auth.model.dao.LogoutAccessToken;
import com.tomtom.scoop.auth.model.dao.RefreshToken;
import com.tomtom.scoop.auth.repository.LogoutAccessTokenRepository;
import com.tomtom.scoop.auth.repository.RefreshTokenRepository;
import com.tomtom.scoop.auth.util.JwtTokenUtil;
import com.tomtom.scoop.domain.user.model.entity.User;
import com.tomtom.scoop.domain.user.repository.UserRepository;
import com.tomtom.scoop.global.exception.BusinessException;
import com.tomtom.scoop.global.exception.ErrorCode;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Autowired
    LogoutAccessTokenRepository logoutAccessTokenRepository;

    @Autowired
    UserRepository userRepository;

    public TokenDto reissue(HttpServletRequest request, HttpServletResponse response) {
        try {
            String token = jwtTokenUtil.resolveToken(request);
            String oauthId = jwtTokenUtil.getOauthId(token);

            Optional<RefreshToken> refreshToken = refreshTokenRepository.findById(oauthId);
            refreshToken.orElseThrow(
                    () -> new BusinessException(ErrorCode.UNAUTHORIZED)
            );

            boolean isTokenValid = jwtTokenUtil.validate(refreshToken.get().getValue(), oauthId);

            if (isTokenValid) {

                Optional<User> findUser = userRepository.findByOauthId(oauthId);

                if (findUser.isPresent()) {
                    String newAccessToken = jwtTokenUtil.generateAccessToken(oauthId);
                    String newRefreshToken = jwtTokenUtil.generateRefreshToken(oauthId);


                    refreshTokenRepository.save(new RefreshToken(oauthId, newRefreshToken, Duration.ofDays(14).toMillis()));

                    return new TokenDto(newAccessToken, newRefreshToken);
                }
            }
        } catch (ExpiredJwtException e) {
            throw new BusinessException(ErrorCode.JWT_REFRESH_TOKEN_EXPIRED);
        }
        return null;
    }

    public void logout(TokenDto tokenDto, String oauthId) {
        String accessToken = tokenDto.getAccessToken();
        long remainTime = jwtTokenUtil.getRemainTime(accessToken);
        refreshTokenRepository.deleteById(oauthId);
        logoutAccessTokenRepository.save(LogoutAccessToken.builder().key(oauthId).value(accessToken).expiredTime(remainTime).build());
    }
}
