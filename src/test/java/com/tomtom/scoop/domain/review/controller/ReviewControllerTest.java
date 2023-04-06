package com.tomtom.scoop.domain.review.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.tomtom.scoop.common.mock.annotation.MockLoginUser;
import com.tomtom.scoop.domain.meeting.model.entity.Meeting;
import com.tomtom.scoop.domain.review.model.dto.request.ReviewRequestDto;
import com.tomtom.scoop.domain.review.model.entity.Review;
import com.tomtom.scoop.domain.review.repository.ReviewRepository;
import com.tomtom.scoop.domain.review.service.ReviewService;
import com.tomtom.scoop.domain.user.model.entity.User;
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
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = {ReviewController.class})
@Import(ReviewController.class)
@DisplayName("[API][Controller] 리뷰 관련 테스트")
public class ReviewControllerTest {


    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mvc;

    @MockBean
    ReviewService reviewService;

    @MockBean
    ReviewRepository reviewRepository;


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
    @DisplayName("[API][POST][Controller] 리뷰 생성 테스트")
    @MockLoginUser
    void tetCreateReview() throws Exception {

        User reviewer = User.builder()
                .id(1L)
                .build();

        User receiver = User.builder()
                .id(2L)
                .build();

        Meeting meeting = Meeting.builder()
                .id(1L)
                .build();

        ReviewRequestDto reviewRequestDto = new ReviewRequestDto(1L, 1L, 5, "test");
        String body = mapper.writeValueAsString(reviewRequestDto);
        System.out.println(body);

        Review response = Review.builder()
                .id(1L)
                .meeting(meeting)
                .reviewer(reviewer)
                .receiver(receiver)
                .rating(5)
                .isReviewerHidden(false)
                .isReceiverHidden(false)
                .comment("test")
                .build();

        given(reviewService.createReview(any(), any())).willReturn(response);

        ResultActions actions = mvc.perform(
                post("/v1/reviews")
                        .with(csrf())
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        actions.andExpect(status().isCreated());
        actions.andExpectAll(
                jsonPath("$.id").value(1L),
                jsonPath("$.meeting.id").value(1L),
                jsonPath("$.reviewer.id").value(1L),
                jsonPath("$.receiver.id").value(2L),
                jsonPath("$.rating").value(5),
                jsonPath("$.isReviewerHidden").value(false),
                jsonPath("$.isReceiverHidden").value(false),
                jsonPath("$.comment").value("test")
        );


    }


}
