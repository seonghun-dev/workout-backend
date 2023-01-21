package com.tomtom.scoop.global.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

public class JwtTokenUtil {

    @Value("${jwt.secret-key}")
    private static String secretKey;

    @Value("${jwt.token.expired-time-ms}")
    private static Long expiredTimeMs;

    private static Key getSigningKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }


    public static String generateAccessToken(String oauthId) {
        return doGenerateToken(oauthId);
    }

    private static String doGenerateToken(String oauthId) {
        Claims claims = Jwts.claims();
        claims.put("oauthId", oauthId);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiredTimeMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

}
