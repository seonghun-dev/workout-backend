package com.tomtom.scoop.auth.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class JwtTokenValidator {

    private final JwtTokenConfig jwtTokenConfig;

    public Boolean validate(String token, String oauthId) {
        try {
            String oauthIdByToken = getOauthId(token);
            return oauthIdByToken.equals(oauthId);
        } catch (ExpiredJwtException ex) {
            return false;
        }
    }

    public String getOauthId(String token) throws ExpiredJwtException {
        return extractAllClaims(token).get("oauthId", String.class);
    }


    private Claims extractAllClaims(String token) {
        return jwtTokenConfig.getJwtParser().parseClaimsJws(token).getBody();
    }

}
