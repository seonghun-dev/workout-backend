package com.tomtom.scoop.domain.meeting.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.tomtom.scoop.common.mock.annotation.MockLoginUser;
import com.tomtom.scoop.domain.meeting.model.dto.response.MeetingKeywordResponseDto;
import com.tomtom.scoop.domain.meeting.service.MeetingKeywordService;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {MeetingKeywordController.class})
@Import(MeetingKeywordController.class)
@DisplayName("[API][Controller] 모임 키워드 관련 테스트")
public class MeetingKeywordControllerTest {
    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mvc;

    @MockBean
    MeetingKeywordService meetingKeywordService;

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
    @DisplayName("[API][GET][Controller] 모임 키워드 전체 조회 테스트")
    @MockLoginUser
    void findAllMeetingKeywords() throws Exception {
        MeetingKeywordResponseDto meetingKeywordResponseDto = MeetingKeywordResponseDto.builder()
                .id(1L)
                .keyword("test")
                .build();

        String body = mapper.writeValueAsString(meetingKeywordResponseDto);

        given(meetingKeywordService.findAllMeetingKeywords()).willReturn(Arrays.asList(meetingKeywordResponseDto));

        mvc.perform(get("/v1/meeting-keywords")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].keyword").value("test"));

    }


}
