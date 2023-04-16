package com.tomtom.scoop.domain.notification.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tomtom.scoop.common.mock.annotation.MockLoginUser;
import com.tomtom.scoop.domain.notification.model.dto.NotificationActionDto;
import com.tomtom.scoop.domain.notification.model.dto.NotificationResponseDto;
import com.tomtom.scoop.domain.notification.repository.NotificationRepository;
import com.tomtom.scoop.domain.notification.service.NotificationService;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {NotificationController.class})
@ActiveProfiles("test")
@Import(NotificationController.class)
@DisplayName("[API][Controller] 알림 관련 테스트")
public class NotificationControllerTest {
    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mvc;

    @MockBean
    NotificationService notificationService;

    @MockBean
    NotificationRepository notificationRepository;

    @Test
    @DisplayName("[API][GET][Controller] 알림 전체 조회 테스트")
    @MockLoginUser
    void testFindAllNotification() throws Exception {
        NotificationActionDto notificationActionDto = NotificationActionDto.builder().page("Meeting").contentId(1L).build();
        List<NotificationResponseDto> notificationListResponseDtoList = List.of(
                NotificationResponseDto.builder().id(1L).title("hello").content("test alert").isRead(false).createdAt(LocalDateTime.of(2022, 3, 1, 3, 0)).action(notificationActionDto).build(),
                NotificationResponseDto.builder().id(2L).title("hello2").content("test alert2").isRead(false).createdAt(LocalDateTime.of(2022, 4, 1, 3, 0)).action(notificationActionDto).build()
        );

        given(notificationService.findAllNotifications(any())).willReturn(notificationListResponseDtoList);

        ResultActions actions = mvc.perform(
                get("/v1/notifications")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        actions.andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$[0].id").value(1L),
                        jsonPath("$[0].title").value("hello"),
                        jsonPath("$[0].content").value("test alert"),
                        jsonPath("$[0].isRead").value(false),
                        jsonPath("$[0].createdAt").value("2022-03-01T03:00:00"),
                        jsonPath("$[0].action.page").value("Meeting"),
                        jsonPath("$[0].action.contentId").value(1L),
                        jsonPath("$[1].id").value(2L),
                        jsonPath("$[1].title").value("hello2"),
                        jsonPath("$[1].content").value("test alert2"),
                        jsonPath("$[1].isRead").value(false),
                        jsonPath("$[1].createdAt").value("2022-04-01T03:00:00"),
                        jsonPath("$[1].action.page").value("Meeting"),
                        jsonPath("$[1].action.contentId").value(1L)
                );


    }

    @Test
    @DisplayName("[API][GET][Controller] 알림 읽음 처리 테스트")
    @MockLoginUser
    void testReadNotification() throws Exception {
        mvc.perform(post("/v1/notifications/1/read").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("[API][GET][Controller] 알림 삭제 테스트")
    @MockLoginUser
    void testDeleteNotification() throws Exception {
        mvc.perform(delete("/v1/notifications/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

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


}
