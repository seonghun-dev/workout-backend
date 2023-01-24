package com.tomtom.scoop.global.security;

import com.tomtom.scoop.global.util.JwtTokenUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@Slf4j
public class OAuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.token.expired-time-ms}")
    private Long expiredTimeMs;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String oauthId = String.valueOf(oAuth2User.getAttributes().get("id"));
        String jwt = JwtTokenUtil.generateAccessToken(oauthId, secretKey, expiredTimeMs);

        String url = makeRedirectUrl(jwt, oauthId);
        log.info(url);

        if (response.isCommitted()) {
            logger.debug("응답이 이미 커밋된 상태입니다. " + url + "로 리다이렉트하도록 바꿀 수 없습니다.");
            return;
        }
        getRedirectStrategy().sendRedirect(request, response, url);
    }

    private String makeRedirectUrl(String token, String oauthId) {

        //TODO

        return UriComponentsBuilder.fromUriString("http://localhost:3000/oauth2/redirect/")
                .queryParam("token", token)
                .build().toUriString();
    }

}
