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

    public static Boolean validate(String token, String oauthId, String key) {
        String oauthIdByToken = getOauthId(token, key);
        return oauthIdByToken.equals(oauthId) && !isTokenExpired(token, key);
    }

    public static Claims extractAllClaims(String token, String key) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey(key))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public static String getOauthId(String token, String key) {
        return extractAllClaims(token,key).get("oauthId", String.class);
    }

    public static Boolean isTokenExpired(String token, String key) {
        Date expiration = extractAllClaims(token, key).getExpiration();
        return expiration.before(new Date());
    }

    private static Key getSigningKey(String key) {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }


    public static String generateAccessToken(String oauthId, String key, long expireTime) {
        return doGenerateToken(oauthId, key, expireTime);
    }

    private static String doGenerateToken(String oauthId, String key, long expireTime) {
        Claims claims = Jwts.claims();
        claims.put("oauthId", oauthId);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expireTime))
                .signWith(getSigningKey(key), SignatureAlgorithm.HS256)
                .compact();
    }

}
