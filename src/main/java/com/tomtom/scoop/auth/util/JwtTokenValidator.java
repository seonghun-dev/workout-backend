package com.tomtom.scoop.auth.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

public class JwtTokenValidator {

    @Value("${jwt.secret-key}")
    private String secretKey;

    private final JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(secretKey).build();

    public Boolean validate(String token, String oauthId) {
        String oauthIdByToken = getOauthId(token);
        return oauthIdByToken.equals(oauthId) && !isTokenExpired(token);
    }

    public String getOauthId(String token) {
        return extractAllClaims(token).get("oauthId", String.class);
    }

    private Boolean isTokenExpired(String token) {
        Date expiration = extractAllClaims(token).getExpiration();
        return expiration.before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return jwtParser.parseClaimsJws(token).getBody();
    }

}
