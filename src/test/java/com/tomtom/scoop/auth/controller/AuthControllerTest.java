package com.tomtom.scoop.auth.controller;

import com.tomtom.scoop.auth.model.TokenDto;
import com.tomtom.scoop.auth.service.AuthService;
import com.tomtom.scoop.common.mock.annotation.MockLoginUser;
import com.tomtom.scoop.global.telegram.TelegramProvider;
import com.tomtom.scoop.infrastructor.telegram.TelegramService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {AuthController.class})
@ActiveProfiles("test")
@Import(AuthController.class)
@DisplayName("[API][Controller] 인증 관련 테스트")
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Configuration
    static class TestConfig {
        @Bean
        public TelegramService telegramService() {
            return new TelegramService();
        }

        @Bean
        public TelegramProvider telegramProvider(TelegramService telegramService) {
            return new TelegramProvider(telegramService);
        }
    }


    @Test
    @MockLoginUser
    public void testReissue() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + "accessToken");

        TokenDto tokenDto = new TokenDto("accessToken", "refreshToken");
        given(authService.reissue(any())).willReturn(tokenDto);

        ResultActions actions = mockMvc.perform(get("/api/auth/reissue")
                .with(csrf())
                .header("Authorization", "Bearer accessToken")
        );

        actions.andExpectAll(
                status().isOk(),
                jsonPath("$.accessToken").value("accessToken"),
                jsonPath("$.refreshToken").value("refreshToken")
        );

    }

}
