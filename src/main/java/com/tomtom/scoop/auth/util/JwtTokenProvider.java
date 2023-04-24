package com.tomtom.scoop.auth.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@AllArgsConstructor
public class JwtTokenProvider {

    private final JwtTokenConfig jwtTokenConfig;

    private String createToken(String oauthId, Long expiredTime) {
        Claims claims = Jwts.claims();
        claims.put("oauthId", oauthId);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiredTime))
                .signWith(jwtTokenConfig.getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateAccessToken(String oauthId) {
        return createToken(oauthId, jwtTokenConfig.getAccessTokenValidTime());
    }

    public String generateRefreshToken(String oauthId) {
        return createToken(oauthId, jwtTokenConfig.getRefreshTokenValidTime());
    }

}
