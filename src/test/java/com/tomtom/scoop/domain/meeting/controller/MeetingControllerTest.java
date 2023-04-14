package com.tomtom.scoop.domain.meeting.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tomtom.scoop.common.mock.annotation.MockLoginUser;
import com.tomtom.scoop.domain.common.Gender;
import com.tomtom.scoop.domain.meeting.model.dto.request.FindAllMeetingRequestDto;
import com.tomtom.scoop.domain.meeting.model.dto.request.MeetingRequestDto;
import com.tomtom.scoop.domain.meeting.model.dto.response.MeetingDetailResponseDto;
import com.tomtom.scoop.domain.meeting.model.dto.response.MeetingListResponseDto;
import com.tomtom.scoop.domain.meeting.service.MeetingService;
import com.tomtom.scoop.domain.user.repository.UserRepository;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {MeetingController.class})
@ActiveProfiles("test")
@Import(MeetingController.class)
@DisplayName("[API][Controller] 모임 관련 테스트")
public class MeetingControllerTest {

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mvc;

    @MockBean
    MeetingService meetingService;

    @MockBean
    UserRepository userRepository;

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
    @DisplayName("[API][POST][Controller] 모임 생성 테스트")
    @MockLoginUser
    void testCreateMeeting() throws Exception {
        MeetingDetailResponseDto meetingResponse = MeetingDetailResponseDto.builder()
                .id(1L)
                .title("Running in the park")
                .content("Let's run together In KonKuk the park")
                .memberLimit(10)
                .memberCount(5)
                .ownerName("koo")
                .ownerProfile("https://scoop.com/42455.png")
                .meetingUserProfiles(Collections.singletonList("https://scoop.com/42455.png"))
                .meetingType("Play")
                .exerciseLevel("Beginner")
                .status("OWNER")
                .isLiked(false)
                .build();

        MeetingRequestDto meetingRequestDto = new MeetingRequestDto(
                "Running in the park",
                "Let's run together In KonKuk the park",
                10,
                Gender.MALE,
                LocalDateTime.of(2023, 5, 5, 12, 0),
                37.5419f,
                127.0738f,
                "KonKuk University",
                "Library",
                "Seoul",
                1L,
                "Play");


        String body = mapper.writeValueAsString(meetingRequestDto);
        System.out.println(body);
        given(meetingService.createMeeting(any(), any())).willReturn(meetingResponse);

        ResultActions actions = mvc.perform(
                post("/v1/meetings")
                        .with(csrf())
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        actions.andExpect(status().isCreated());

        actions.andExpectAll(
                jsonPath("$.id").value(1L),
                jsonPath("$.title").value("Running in the park"),
                jsonPath("$.content").value("Let's run together In KonKuk the park"),
                jsonPath("$.memberLimit").value(10),
                jsonPath("$.memberCount").value(5),
                jsonPath("$.ownerName").value("koo"),
                jsonPath("$.ownerProfile").value("https://scoop.com/42455.png"),
                jsonPath("$.meetingUserProfiles[0]").value("https://scoop.com/42455.png"),
                jsonPath("$.meetingType").value("Play"),
                jsonPath("$.exerciseLevel").value("Beginner"),
                jsonPath("$.status").value("OWNER"),
                jsonPath("$.isLiked").value(false)
        );
    }

    @Test
    @DisplayName("[API][GET][Controller] 모임 전체 조회 테스트")
    @MockLoginUser
    void testGetAllMeetings() throws Exception {
        FindAllMeetingRequestDto query = new FindAllMeetingRequestDto(Optional.of(1), Optional.of(10));
        String body = mapper.writeValueAsString(query);


        MeetingListResponseDto element = new MeetingListResponseDto(
                1L,
                "Running in the park",
                "seoul",
                LocalDateTime.of(2023, 5, 5, 12, 0),
                5,
                10,
                "https://scoop.com/42455.png",
                "Running",
                "Beginner",
                "Play");

        List<MeetingListResponseDto> result = Collections.singletonList(element);

        given(meetingService.findAllMeetings(any())).willReturn(result);
        System.out.println("result" + result);


        ResultActions actions = mvc.perform(
                get("/v1/meetings")
                        .with(csrf())
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$").isArray());
        actions.andExpectAll(
                jsonPath("$[0].id").value(1L),
                jsonPath("$[0].title").value("Running in the park"),
                jsonPath("$[0].city").value("seoul"),
                jsonPath("$[0].eventDate").value("2023-05-05T12:00:00"),
                jsonPath("$[0].memberCount").value(5),
                jsonPath("$[0].memberLimit").value(10),
                jsonPath("$[0].imgUrl").value("https://scoop.com/42455.png"),
                jsonPath("$[0].exerciseName").value("Running"),
                jsonPath("$[0].exerciseLevel").value("Beginner"),
                jsonPath("$[0].meetingType").value("Play")
        );
    }

    @Test
    @DisplayName("[API][GET][Controller] 모임 단건 조회 테스트")
    @MockLoginUser
    void testGetMeetingDetail() throws Exception {

        Long meetingId = 1L;

        MeetingDetailResponseDto meetingResponse = MeetingDetailResponseDto.builder()
                .id(1L)
                .title("Running in the park")
                .content("Let's run together In KonKuk the park")
                .memberLimit(10)
                .memberCount(5)
                .ownerName("koo")
                .ownerProfile("https://scoop.com/42455.png")
                .meetingUserProfiles(Collections.singletonList("https://scoop.com/42455.png"))
                .meetingType("Play")
                .exerciseLevel("Beginner")
                .status("OWNER")
                .isLiked(false)
                .build();

        given(meetingService.findMeetingById(meetingId)).willReturn(meetingResponse);

        ResultActions actions = mvc.perform(
                get("/v1/meetings/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        actions.andExpect(status().isOk());

        actions.andExpectAll(
                jsonPath("$.id").value(1L),
                jsonPath("$.title").value("Running in the park"),
                jsonPath("$.content").value("Let's run together In KonKuk the park"),
                jsonPath("$.memberLimit").value(10),
                jsonPath("$.memberCount").value(5),
                jsonPath("$.ownerName").value("koo"),
                jsonPath("$.ownerProfile").value("https://scoop.com/42455.png"),
                jsonPath("$.meetingUserProfiles[0]").value("https://scoop.com/42455.png"),
                jsonPath("$.meetingType").value("Play"),
                jsonPath("$.exerciseLevel").value("Beginner"),
                jsonPath("$.status").value("OWNER"),
                jsonPath("$.isLiked").value(false)
        );
    }


    @Test
    @DisplayName("[API][DELETE][Controller] 모임 삭제 테스트")
    @MockLoginUser
    void testDeleteMeeting() throws Exception {

        ResultActions actions = mvc.perform(
                delete("/v1/meetings/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        actions.andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("[API][POST][Controller] 모임 가입 테스트")
    @MockLoginUser
    void testJoinMeeting() throws Exception {

        ResultActions actions = mvc.perform(
                post("/v1/meetings/1/join")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        actions.andExpect(status().isOk());
    }

    @Test
    @DisplayName("[API][DELETE][Controller] 모임 탈퇴 테스트")
    @MockLoginUser
    void testLeaveMeeting() throws Exception {

        ResultActions actions = mvc.perform(
                post("/v1/meetings/1/quit")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        actions.andExpect(status().isOk());
    }


    @Test
    @DisplayName("[API][POST][Controller] 모임 가입 허가 테스트")
    @MockLoginUser
    void testApproveMeeting() throws Exception {

        ResultActions actions = mvc.perform(
                post("/v1/meetings/1/user/2/accept")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        actions.andExpect(status().isOk());
    }

    @Test
    @DisplayName("[API][POST][Controller] 모임 가입 거절 테스트")
    @MockLoginUser
    void testRejectMeeting() throws Exception {

        ResultActions actions = mvc.perform(
                post("/v1/meetings/1/user/2/reject")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        actions.andExpect(status().isOk());
    }

    @Test
    @DisplayName("[API][GET][Controller] 유저별 참여 예정 모임 조회 테스트")
    @MockLoginUser
    void testGetUserMeetings() throws Exception {

        MeetingListResponseDto element = new MeetingListResponseDto(
                1L,
                "Running in the park",
                "seoul",
                LocalDateTime.of(2023, 5, 5, 12, 0),
                5,
                10,
                "https://scoop.com/42455.png",
                "Running",
                "Beginner",
                "Play");

        List<MeetingListResponseDto> result = Collections.singletonList(element);

        given(meetingService.findUserUpcomingMeeting(any(), any())).willReturn(result);

        ResultActions actions = mvc.perform(
                get("/v1/meetings/upcoming")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$").isArray());
        actions.andExpectAll(
                jsonPath("$[0].id").value(1L),
                jsonPath("$[0].title").value("Running in the park"),
                jsonPath("$[0].city").value("seoul"),
                jsonPath("$[0].eventDate").value("2023-05-05T12:00:00"),
                jsonPath("$[0].memberCount").value(5),
                jsonPath("$[0].memberLimit").value(10),
                jsonPath("$[0].imgUrl").value("https://scoop.com/42455.png"),
                jsonPath("$[0].exerciseName").value("Running"),
                jsonPath("$[0].exerciseLevel").value("Beginner"),
                jsonPath("$[0].meetingType").value("Play")
        );
    }


    @Test
    @DisplayName("[API][GET][Controller] 유저별 참여 완료 모임 조회 테스트")
    @MockLoginUser
    void testGetUserCompletedMeetings() throws Exception {

        MeetingListResponseDto element = new MeetingListResponseDto(
                1L,
                "Running in the park",
                "seoul",
                LocalDateTime.of(2023, 5, 5, 12, 0),
                5,
                10,
                "https://scoop.com/42455.png",
                "Running",
                "Beginner",
                "Play");

        List<MeetingListResponseDto> result = Collections.singletonList(element);

        given(meetingService.findUserPastMeeting(any(), any())).willReturn(result);

        ResultActions actions = mvc.perform(
                get("/v1/meetings/past")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$").isArray());
        actions.andExpectAll(
                jsonPath("$[0].id").value(1L),
                jsonPath("$[0].title").value("Running in the park"),
                jsonPath("$[0].city").value("seoul"),
                jsonPath("$[0].eventDate").value("2023-05-05T12:00:00"),
                jsonPath("$[0].memberCount").value(5),
                jsonPath("$[0].memberLimit").value(10),
                jsonPath("$[0].imgUrl").value("https://scoop.com/42455.png"),
                jsonPath("$[0].exerciseName").value("Running"),
                jsonPath("$[0].exerciseLevel").value("Beginner"),
                jsonPath("$[0].meetingType").value("Play")
        );
    }


    @Test
    @DisplayName("[API][GET][Controller] 유저별 참여 대기 모임 조회 테스트")
    @MockLoginUser
    void testGetUserWaitingMeetings() throws Exception {

        MeetingListResponseDto element = new MeetingListResponseDto(
                1L,
                "Running in the park",
                "seoul",
                LocalDateTime.of(2023, 5, 5, 12, 0),
                5,
                10,
                "https://scoop.com/42455.png",
                "Running",
                "Beginner",
                "Play");

        List<MeetingListResponseDto> result = Collections.singletonList(element);

        given(meetingService.findUserWaitingMeeting(any(), any())).willReturn(result);

        ResultActions actions = mvc.perform(
                get("/v1/meetings/waiting")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$").isArray());
        actions.andExpectAll(
                jsonPath("$[0].id").value(1L),
                jsonPath("$[0].title").value("Running in the park"),
                jsonPath("$[0].city").value("seoul"),
                jsonPath("$[0].eventDate").value("2023-05-05T12:00:00"),
                jsonPath("$[0].memberCount").value(5),
                jsonPath("$[0].memberLimit").value(10),
                jsonPath("$[0].imgUrl").value("https://scoop.com/42455.png"),
                jsonPath("$[0].exerciseName").value("Running"),
                jsonPath("$[0].exerciseLevel").value("Beginner"),
                jsonPath("$[0].meetingType").value("Play")
        );
    }

    @Test
    @DisplayName("[API][GET][Controller] 유저 주최 모임 조회 테스트")
    @MockLoginUser
    void testGetUserHostMeetings() throws Exception {

        MeetingListResponseDto element = new MeetingListResponseDto(
                1L,
                "Running in the park",
                "seoul",
                LocalDateTime.of(2023, 5, 5, 12, 0),
                5,
                10,
                "https://scoop.com/42455.png",
                "Running",
                "Beginner",
                "Play");

        List<MeetingListResponseDto> result = Collections.singletonList(element);

        given(meetingService.findOwnerMeetingByUser(any(), any())).willReturn(result);

        ResultActions actions = mvc.perform(
                get("/v1/meetings/owner")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$").isArray());
        actions.andExpectAll(
                jsonPath("$[0].id").value(1L),
                jsonPath("$[0].title").value("Running in the park"),
                jsonPath("$[0].city").value("seoul"),
                jsonPath("$[0].eventDate").value("2023-05-05T12:00:00"),
                jsonPath("$[0].memberCount").value(5),
                jsonPath("$[0].memberLimit").value(10),
                jsonPath("$[0].imgUrl").value("https://scoop.com/42455.png"),
                jsonPath("$[0].exerciseName").value("Running"),
                jsonPath("$[0].exerciseLevel").value("Beginner"),
                jsonPath("$[0].meetingType").value("Play")
        );
    }

    @Test
    @DisplayName("[API][GET][Controller] 유저별 좋아요한 모임 조회 테스트")
    @MockLoginUser
    void testGetUserLikeMeetings() throws Exception {

        MeetingListResponseDto element = new MeetingListResponseDto(
                1L,
                "Running in the park",
                "seoul",
                LocalDateTime.of(2023, 5, 5, 12, 0),
                5,
                10,
                "https://scoop.com/42455.png",
                "Running",
                "Beginner",
                "Play");

        List<MeetingListResponseDto> result = Collections.singletonList(element);

        given(meetingService.findLikeMeetingByUser(any(), any())).willReturn(result);

        ResultActions actions = mvc.perform(
                get("/v1/meetings/like")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$").isArray());
        actions.andExpectAll(
                jsonPath("$[0].id").value(1L),
                jsonPath("$[0].title").value("Running in the park"),
                jsonPath("$[0].city").value("seoul"),
                jsonPath("$[0].eventDate").value("2023-05-05T12:00:00"),
                jsonPath("$[0].memberCount").value(5),
                jsonPath("$[0].memberLimit").value(10),
                jsonPath("$[0].imgUrl").value("https://scoop.com/42455.png"),
                jsonPath("$[0].exerciseName").value("Running"),
                jsonPath("$[0].exerciseLevel").value("Beginner"),
                jsonPath("$[0].meetingType").value("Play")
        );
    }

    @Test
    @DisplayName("[API][POST][Controller] 모임 좋아요 테스트")
    @MockLoginUser
    void testLikeMeeting() throws Exception {


        ResultActions actions = mvc.perform(
                post("/v1/meetings/1/like")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        actions.andExpect(status().isOk());
    }

    @Test
    @DisplayName("[API][POST][Controller] 모임 좋아요 취소 테스트")
    @MockLoginUser
    void testUnlikeMeeting() throws Exception {

        ResultActions actions = mvc.perform(
                post("/v1/meetings/1/unlike")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        actions.andExpect(status().isOk());
    }


    @Test
    @DisplayName("[API][POST][Controller] 모임 키워드로 조회 테스트")
    @MockLoginUser
    void testSearchMeeting() throws Exception {

        ResultActions actions = mvc.perform(
                get("/v1/meetings/search")
                        .param("keyword", "Running")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        actions.andExpect(status().isOk());
    }


}
