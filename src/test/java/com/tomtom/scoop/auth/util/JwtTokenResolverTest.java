package com.tomtom.scoop.auth.util;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class JwtTokenResolverTest {

    @Mock
    private HttpServletRequest request;

    private JwtTokenResolver jwtTokenResolver;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        jwtTokenResolver = new JwtTokenResolver();
    }

    @Test
    @DisplayName("resolveToken 메서드 테스트 - Authorization 헤더가 있는 경우")
    public void testResolveTokenWithValidBearerToken() {
        String token = "validToken";
        String bearerToken = "Bearer " + token;
        when(request.getHeader("Authorization")).thenReturn(bearerToken);

        Optional<String> resolvedToken = jwtTokenResolver.resolveToken(request);
        assertTrue(resolvedToken.isPresent());
        assertEquals(token, resolvedToken.get());
    }

    @Test
    @DisplayName("resolveToken 메서드 테스트 - Authorization 헤더가 없는 경우")
    public void testResolveTokenWithEmptyAuthorizationHeader() {
        when(request.getHeader("Authorization")).thenReturn(null);

        Optional<String> resolvedToken = jwtTokenResolver.resolveToken(request);
        assertFalse(resolvedToken.isPresent());
    }

    @Test
    @DisplayName("resolveToken 메서드 테스트 - Authorization 헤더가 Bearer로 시작하지 않는 경우")
    public void testResolveTokenWithInvalidBearerToken() {
        String bearerToken = "InvalidBearerToken";
        when(request.getHeader("Authorization")).thenReturn(bearerToken);

        Optional<String> resolvedToken = jwtTokenResolver.resolveToken(request);
        assertFalse(resolvedToken.isPresent());
    }


}
