package com.tomtom.scoop.domain.review.service;

import com.tomtom.scoop.domain.meeting.model.entity.Meeting;
import com.tomtom.scoop.domain.meeting.model.entity.UserMeeting;
import com.tomtom.scoop.domain.meeting.repository.MeetingRepository;
import com.tomtom.scoop.domain.review.model.dto.request.ReviewRequestDto;
import com.tomtom.scoop.domain.review.model.entity.Review;
import com.tomtom.scoop.domain.review.repository.ReviewRepository;
import com.tomtom.scoop.domain.user.model.entity.User;
import com.tomtom.scoop.domain.user.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("[API][Service] 리뷰 관련 테스트")
public class ReviewServiceTest {

    @Mock
    ReviewRepository reviewRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    MeetingRepository meetingRepository;

    @InjectMocks
    private ReviewService reviewService;


    @Nested
    @DisplayName("[API][Service] 리뷰 생성 테스트")
    class CreateReview {
        private User user;
        private User receiver;
        private Meeting meeting;
        private Review review;
        private List<UserMeeting> userMeetings;
        private ReviewRequestDto reviewRequestDto;

        @BeforeEach
        void setUp() {
            user = User.builder().id(1L).build();
            receiver = User.builder().id(2L).build();
            reviewRequestDto = ReviewRequestDto.builder()
                    .receiverId(2L)
                    .meetingId(3L)
                    .comment("good")
                    .rating(5)
                    .build();
            meeting = Meeting.builder().id(3L).build();

            review = Review.builder()
                    .id(1L)
                    .comment("good")
                    .rating(5)
                    .reviewer(user)
                    .receiver(receiver)
                    .meeting(meeting)
                    .build();

            userMeetings = List.of(UserMeeting.builder().user(user).build(),
                    UserMeeting.builder().user(receiver).build());


        }

        @Nested
        @DisplayName("[API][Service] 리뷰 생성 성공 테스트")
        class Success {

            @Test
            @DisplayName("[API][Service] 리뷰 생성 성공 테스트")
            void createReview() {
                when(userRepository.findById(any())).thenReturn(Optional.of(user));
                when(meetingRepository.findById(any())).thenReturn(Optional.of(meeting));
                when(reviewRepository.save(any(Review.class))).thenReturn(review);

                var result = reviewService.createReview(user, reviewRequestDto);

                Assertions.assertThat(result).isEqualTo(review);
            }

        }


        @Nested
        @DisplayName("[API][Service] 리뷰 생성 실패 테스트")
        class Fail {

            @Test
            @DisplayName("[API][Service] 리뷰를 받는 유저의 ID가 존재하지 않을 경우")
            void createReviewFail() {
            }

            @Test
            @DisplayName("[API][Service] 모임의 ID가 존재하지 않은 경우")
            void createReviewFail2() {
            }

            @Test
            @DisplayName("[API][Service] 리뷰를 작성하는 유저가 모임에 참여하지 않은 경우")
            void createReviewFail3() {
            }

            @Test
            @DisplayName("[API][Service] 리뷰를 받는 유저가 모임에 참여하지 않은 경우")
            void createReviewFail4() {
            }

            @Test
            @DisplayName("[API][Service] 리뷰를 작성하는 유저가 리뷰를 받는 유저와 같은 경우")
            void createReviewFail5() {
            }

            @Test
            @DisplayName("[API][Service] 리뷰를 작성하는 유저가 이미 리뷰를 작성한 경우")
            void createReviewFail6() {
            }
        }
    }


}
