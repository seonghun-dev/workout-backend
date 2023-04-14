package com.tomtom.scoop.domain.user.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.tomtom.scoop.common.mock.annotation.MockLoginUser;
import com.tomtom.scoop.domain.common.Gender;
import com.tomtom.scoop.domain.user.model.dto.ExerciseLevelDto;
import com.tomtom.scoop.domain.user.model.dto.UserLocationDto;
import com.tomtom.scoop.domain.user.model.dto.request.UserJoinDto;
import com.tomtom.scoop.domain.user.model.dto.request.UserUpdateDto;
import com.tomtom.scoop.domain.user.model.dto.response.UserResponseDto;
import com.tomtom.scoop.domain.user.repository.UserRepository;
import com.tomtom.scoop.domain.user.service.UserService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {UserController.class})
@ActiveProfiles("test")
@Import(UserController.class)
@DisplayName("[API][Controller] 유저 관련 테스트")
public class UserControllerTest {

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mvc;

    @MockBean
    UserService userService;

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
    @DisplayName("[API][POST][Controller] 유저 가입 테스트")
    @MockLoginUser
    void testUserJoin() throws Exception {
        ExerciseLevelDto exerciseLevelDto = new ExerciseLevelDto("Soccer", "Beginner");
        UserLocationDto userLocationDto = UserLocationDto.builder().county("Korea").city("Seoul").latitude(37.5419F).longitude(127.0738F).build();
        UserJoinDto userJoinDto = UserJoinDto.builder().name("hog").phone("010-1234-3456").nickname("hok")
                .gender(Gender.MALE).deviceToken("deviceToken")
                .exerciseLevels(List.of(1)).userLocation(userLocationDto)
                .keywords(List.of("ISTP", "Poor")).build();

        UserResponseDto userResponseDto = UserResponseDto.builder().id(1L).name("hog").phone("010-1234-3456").nickname("hok")
                .rating(0F).gender(Gender.MALE).profileImg("https://scoop.com/image").statusMessage("")
                .userExerciseLevels(List.of(exerciseLevelDto))
                .userLocation(userLocationDto)
                .userKeywords(List.of("ISTP", "Poor"))
                .build();

        String body = mapper.writeValueAsString(userJoinDto);

        given(userService.join(any(), any())).willReturn(userResponseDto);

        ResultActions actions = mvc.perform(
                post("/v1/user/join")
                        .with(csrf())
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        actions.andExpect(status().isCreated());

        actions.andExpectAll(
                jsonPath("$.id").value(1L),
                jsonPath("$.name").value("hog"),
                jsonPath("$.phone").value("010-1234-3456"),
                jsonPath("$.nickname").value("hok"),
                jsonPath("$.rating").value(0F),
                jsonPath("$.gender").value("MALE"),
                jsonPath("$.profileImg").value("https://scoop.com/image"),
                jsonPath("$.statusMessage").value(""),
                jsonPath("$.userExerciseLevels.[0].exerciseName").value("Soccer"),
                jsonPath("$.userExerciseLevels.[0].level").value("Beginner"),
                jsonPath("$.userLocation.county").value("Korea"),
                jsonPath("$.userLocation.city").value("Seoul"),
                jsonPath("$.userLocation.latitude").value(37.5419F),
                jsonPath("$.userLocation.longitude").value(127.0738F),
                jsonPath("$.userKeywords[0]").value("ISTP"),
                jsonPath("$.userKeywords[1]").value("Poor")
        );
    }


    @Test
    @DisplayName("[API][POST][Controller] 유저 정보 업데이트 테스트")
    @MockLoginUser
    void testUpdateUserInfo() throws Exception {
        UserUpdateDto userUpdateDto = UserUpdateDto.builder().nickname("hok").statusMessage("talk").profileImgUrl("https://scoop.com/image").exerciseLevels(List.of(1, 2)).keywords(List.of("ISTP", "Poor")).build();
        UserResponseDto userResponseDto = UserResponseDto.builder().id(1L).name("hog").phone("010-1234-3456").nickname("hok")
                .rating(2F).gender(Gender.MALE).profileImg("https://scoop.com/image").build();

        String body = mapper.writeValueAsString(userUpdateDto);
        given(userService.update(any(), any())).willReturn(userResponseDto);

        ResultActions actions = mvc.perform(
                post("/v1/user/update")
                        .with(csrf())
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        actions.andExpect(status().isOk());


        actions.andExpectAll(
                jsonPath("$.id").value(1L),
                jsonPath("$.name").value("hog"),
                jsonPath("$.phone").value("010-1234-3456"),
                jsonPath("$.nickname").value("hok"),
                jsonPath("$.rating").value(2F),
                jsonPath("$.gender").value("MALE"),
                jsonPath("$.profileImg").value("https://scoop.com/image")
        );


    }

    @Test
    @DisplayName("[API][POST][Controller] 유저 정보 가져오기 테스트")
    @MockLoginUser
    void testGetUserInfo() throws Exception {
        ExerciseLevelDto exerciseLevelDto = new ExerciseLevelDto("Soccer", "Beginner");
        UserLocationDto userLocationDto = UserLocationDto.builder().county("Korea").city("Seoul").latitude(37.5419F).longitude(127.0738F).build();
        UserResponseDto userResponseDto = UserResponseDto.builder().id(1L).name("hog").phone("010-1234-3456").nickname("hok")
                .rating(0F).gender(Gender.MALE).profileImg("https://scoop.com/image").statusMessage("")
                .userExerciseLevels(List.of(exerciseLevelDto))
                .userLocation(userLocationDto)
                .userKeywords(List.of("ISTP", "Poor"))
                .build();

        given(userService.me(any())).willReturn(userResponseDto);

        ResultActions actions = mvc.perform(
                        get("/v1/user"));

        actions.andExpect(status().isOk());

        actions.andExpectAll(
                jsonPath("$.id").value(1L),
                jsonPath("$.name").value("hog"),
                jsonPath("$.phone").value("010-1234-3456"),
                jsonPath("$.nickname").value("hok"),
                jsonPath("$.rating").value(0F),
                jsonPath("$.gender").value("MALE"),
                jsonPath("$.profileImg").value("https://scoop.com/image"),
                jsonPath("$.statusMessage").value(""),
                jsonPath("$.userExerciseLevels.[0].exerciseName").value("Soccer"),
                jsonPath("$.userExerciseLevels.[0].level").value("Beginner"),
                jsonPath("$.userLocation.county").value("Korea"),
                jsonPath("$.userLocation.city").value("Seoul"),
                jsonPath("$.userLocation.latitude").value(37.5419F),
                jsonPath("$.userLocation.longitude").value(127.0738F),
                jsonPath("$.userKeywords[0]").value("ISTP"),
                jsonPath("$.userKeywords[1]").value("Poor")
        );
    }






    @Test
    @DisplayName("[API][PATCH][Controller] 유저 지역 정보 업데이트 테스트")
    @MockLoginUser
    void testUpdateUserLocation() throws Exception {
        UserLocationDto userLocationDto = UserLocationDto.builder().county("Korea").city("Seoul").latitude(37.5419F).longitude(127.0738F).build();
        UserResponseDto userResponseDto = UserResponseDto.builder().id(1L).name("hog").phone("010-1234-3456").nickname("hok")
                .rating(2F).gender(Gender.MALE).profileImg("https://scoop.com/image").userLocation(userLocationDto).build();

        String body = mapper.writeValueAsString(userLocationDto);
        given(userService.updateUserLocation(any(), any())).willReturn(userResponseDto);

        ResultActions actions = mvc.perform(
                patch("/v1/user/location")
                        .with(csrf())
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        actions.andExpect(status().isOk());

        actions.andExpectAll(
                jsonPath("$.id").value(1L),
                jsonPath("$.name").value("hog"),
                jsonPath("$.phone").value("010-1234-3456"),
                jsonPath("$.nickname").value("hok"),
                jsonPath("$.rating").value(2F),
                jsonPath("$.gender").value("MALE"),
                jsonPath("$.profileImg").value("https://scoop.com/image"),
                jsonPath("$.userLocation.county").value("Korea"),
                jsonPath("$.userLocation.city").value("Seoul"),
                jsonPath("$.userLocation.latitude").value(37.5419F),
                jsonPath("$.userLocation.longitude").value(127.0738F)
        );

    }
}
