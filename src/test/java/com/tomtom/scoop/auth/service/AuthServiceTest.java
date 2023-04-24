package com.tomtom.scoop.auth.service;

import com.tomtom.scoop.auth.model.TokenDto;
import com.tomtom.scoop.auth.model.dao.RefreshToken;
import com.tomtom.scoop.auth.repository.RefreshTokenRepository;
import com.tomtom.scoop.auth.util.JwtTokenProvider;
import com.tomtom.scoop.auth.util.JwtTokenResolver;
import com.tomtom.scoop.auth.util.JwtTokenValidator;
import com.tomtom.scoop.domain.user.model.entity.User;
import com.tomtom.scoop.domain.user.repository.UserRepository;
import com.tomtom.scoop.global.exception.BusinessException;
import com.tomtom.scoop.global.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("[API][Service] 인증 관련 테스트")
public class AuthServiceTest {

    @Mock
    JwtTokenProvider jwtTokenProvider;

    @Mock
    JwtTokenValidator jwtTokenValidator;

    @Mock
    JwtTokenResolver jwtTokenResolver;

    @Mock
    RefreshTokenRepository refreshTokenRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    private AuthService authService;

    @Nested
    @DisplayName("[API][Service] Reissue 테스트")
    class Reissue {
        @Nested
        @DisplayName("[API][Service] Reissue 성공 테스트")
        class Success {

            @Test
            @DisplayName("[API][Service] Reissue - 성공: 새로운 액세스 토큰 발급 및 리프레시 토큰 갱신")
            void shouldReissueTokens() {
                String oauthId = "test-oauth-id";
                String refreshTokenValue = "test-refresh-token";
                RefreshToken refreshToken = new RefreshToken(oauthId, refreshTokenValue, System.currentTimeMillis());

                when(jwtTokenResolver.resolveToken(any(HttpServletRequest.class))).thenReturn(Optional.of("test-access-token"));
                when(jwtTokenValidator.getOauthId(anyString())).thenReturn(oauthId);
                when(refreshTokenRepository.findById(oauthId)).thenReturn(Optional.of(refreshToken));
                when(jwtTokenValidator.validate(refreshTokenValue, oauthId)).thenReturn(true);
                when(userRepository.findByOauthId(oauthId)).thenReturn(Optional.of(new User()));
                when(jwtTokenProvider.generateAccessToken(oauthId)).thenReturn("new-access-token");
                when(jwtTokenProvider.generateRefreshToken(oauthId)).thenReturn("new-refresh-token");

                TokenDto result = authService.reissue(mock(HttpServletRequest.class));

                Assertions.assertThat(result).isNotNull();
                Assertions.assertThat(result.getAccessToken()).isEqualTo("new-access-token");
                Assertions.assertThat(result.getRefreshToken()).isEqualTo("new-refresh-token");
            }

        }

        @Nested
        @DisplayName("[API][Service] Reissue 실패 테스트")
        class Fail {

            String oauthId;
            String accessTokenValue;
            String refreshTokenValue;

            @BeforeEach
            void setup() {
                oauthId = "test-oauth-id";
                accessTokenValue = "test-access-token";
                refreshTokenValue = "test-refresh-token";
            }

            @Test
            @DisplayName("[API][Service] Reissue - 실패: 리프레시 토큰이 존재하지 않음")
            void shouldThrowExceptionWhenRefreshTokenNotFound() {
                when(jwtTokenResolver.resolveToken(any(HttpServletRequest.class))).thenReturn(Optional.of(accessTokenValue));
                when(jwtTokenValidator.getOauthId(anyString())).thenReturn(oauthId);
                when(refreshTokenRepository.findById(anyString())).thenReturn(Optional.empty());

                BusinessException exception = assertThrows(BusinessException.class, () -> authService.reissue(mock(HttpServletRequest.class)));
                Assertions.assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.JWT_REFRESH_TOKEN_EXPIRED);
            }

            @Test
            @DisplayName("[API][Service] Reissue - 실패: 리프레시 토큰이 만료됨")
            void shouldThrowExceptionWhenRefreshTokenIsExpired() {
                RefreshToken refreshToken = new RefreshToken(oauthId, refreshTokenValue, System.currentTimeMillis() - 10000);

                when(jwtTokenResolver.resolveToken(any(HttpServletRequest.class))).thenReturn(Optional.of(accessTokenValue));
                when(jwtTokenValidator.getOauthId(anyString())).thenReturn(oauthId);
                when(refreshTokenRepository.findById(oauthId)).thenReturn(Optional.of(refreshToken));
                when(jwtTokenValidator.validate(refreshTokenValue, oauthId)).thenReturn(false);

                BusinessException exception = assertThrows(BusinessException.class, () -> authService.reissue(mock(HttpServletRequest.class)));
                Assertions.assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_TOKEN);
            }

            @Test
            @DisplayName("[API][Service] Reissue - 실패: 인증된 사용자가 없음")
            void shouldThrowExceptionWhenNoAuthenticatedUser() {
                RefreshToken refreshToken = new RefreshToken(oauthId, refreshTokenValue, System.currentTimeMillis());

                when(jwtTokenResolver.resolveToken(any(HttpServletRequest.class))).thenReturn(Optional.of(accessTokenValue));
                when(jwtTokenValidator.getOauthId(anyString())).thenReturn(oauthId);
                when(refreshTokenRepository.findById(oauthId)).thenReturn(Optional.of(refreshToken));
                when(jwtTokenValidator.validate(refreshTokenValue, oauthId)).thenReturn(true);
                when(userRepository.findByOauthId(any())).thenReturn(Optional.empty());

                BusinessException exception = assertThrows(BusinessException.class, () -> authService.reissue(mock(HttpServletRequest.class)));
                Assertions.assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
            }

        }

    }


}
