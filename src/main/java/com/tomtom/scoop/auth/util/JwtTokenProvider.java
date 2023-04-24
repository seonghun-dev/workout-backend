package com.tomtom.scoop.auth.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final long accessTokenValidTime = Duration.ofMinutes(30).toMillis();
    private final long refreshTokenValidTime = Duration.ofDays(14).toMillis();
    @Value("${jwt.secret-key}")
    private String secretKey;

    public Boolean validate(String token, String oauthId) {
        String oauthIdByToken = getOauthId(token);
        return oauthIdByToken.equals(oauthId) && !isTokenExpired(token);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getOauthId(String token) {
        return extractAllClaims(token).get("oauthId", String.class);
    }

    public Boolean isTokenExpired(String token) {
        Date expiration = extractAllClaims(token).getExpiration();
        return expiration.before(new Date());
    }

    private Key getSigningKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }


    public String generateAccessToken(String oauthId) {
        return createToken(oauthId, accessTokenValidTime);
    }

    public String generateRefreshToken(String oauthId) {
        return createToken(oauthId, refreshTokenValidTime);
    }

    private String createToken(String oauthId, Long expiredTime) {
        Claims claims = Jwts.claims();
        claims.put("oauthId", oauthId);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiredTime))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

}
