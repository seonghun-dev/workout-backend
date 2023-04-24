package com.tomtom.scoop.auth.util;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("[API][Util] JWT 토큰 테스트 - JwtTokenProvider, JwtTokenResolver, JwtTokenValidator")
class JwtTokenTest {

    @Spy
    private JwtTokenConfig jwtTokenConfig = new JwtTokenConfig(1000000L, 10000000L, "tatokentestfortestinguserthisisjwttokentestkeytest");

    @InjectMocks
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private JwtTokenValidator jwtTokenValidator;


    @Test
    @DisplayName("Access Token 생성 테스트")
    void testGenerateAccessToken() {
        String oauthId = "testId";

        var result = jwtTokenProvider.generateAccessToken(oauthId);

        Assertions.assertThat(result).isNotNull();

        var validateResult = jwtTokenValidator.validate(result, oauthId);
        Assertions.assertThat(validateResult).isTrue();

        var oauthIdByToken = jwtTokenValidator.getOauthId(result);
        Assertions.assertThat(oauthIdByToken).isEqualTo(oauthId);
    }

    @Test
    @DisplayName("Refresh Token 생성 테스트")
    void testGenerateRefreshToken() {
        String oauthId = "testId";
        var result = jwtTokenProvider.generateRefreshToken(oauthId);

        Assertions.assertThat(result).isNotNull();

        var validateResult = jwtTokenValidator.validate(result, oauthId);
        Assertions.assertThat(validateResult).isTrue();

        var oauthIdByToken = jwtTokenValidator.getOauthId(result);
        Assertions.assertThat(oauthIdByToken).isEqualTo(oauthId);
    }

    @Test
    @DisplayName("validate에서 oauthId가 일치하지 않는 경우 false를 반환하는지 테스트")
    void testValidateWithDifferentOauthId() {
        String oauthId = "testId";
        String token = jwtTokenProvider.generateAccessToken(oauthId);

        var result = jwtTokenValidator.validate(token, "differentOauthId");

        Assertions.assertThat(result).isFalse();
    }


    @Nested
    class testClass {
        @Spy
        private JwtTokenConfig jwtTokenConfig = new JwtTokenConfig(0L, 0L, "tatokentestfortestinguserthisisjwttokentestkeytest");
        @InjectMocks
        private JwtTokenProvider jwtTokenProvider;

        @InjectMocks
        private JwtTokenValidator jwtTokenValidator;

        @Test
        @DisplayName("validate에서 토큰이 만료된 경우 false를 반환하는지 테스트")
        void testValidateWithExpiredToken() {
            String oauthId = "testId";
            String token = jwtTokenProvider.generateAccessToken(oauthId);

            var result = jwtTokenValidator.validate(token, oauthId);

            Assertions.assertThat(result).isFalse();
        }

    }


}