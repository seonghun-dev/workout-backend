package com.tomtom.scoop.domain.review.service;

import com.tomtom.scoop.domain.meeting.model.entity.Meeting;
import com.tomtom.scoop.domain.meeting.model.entity.UserMeeting;
import com.tomtom.scoop.domain.meeting.repository.MeetingRepository;
import com.tomtom.scoop.domain.meeting.repository.UserMeetingRepository;
import com.tomtom.scoop.domain.review.model.dto.request.ReviewRequestDto;
import com.tomtom.scoop.domain.review.model.entity.Review;
import com.tomtom.scoop.domain.review.repository.ReviewRepository;
import com.tomtom.scoop.domain.user.model.entity.User;
import com.tomtom.scoop.domain.user.repository.UserRepository;
import com.tomtom.scoop.global.exception.BusinessException;
import com.tomtom.scoop.global.exception.NotFoundException;
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

import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @Mock
    UserMeetingRepository userMeetingRepository;

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
                when(userRepository.findById(any())).thenReturn(Optional.of(receiver));
                when(meetingRepository.findById(any())).thenReturn(Optional.of(meeting));
                when(reviewRepository.save(any(Review.class))).thenReturn(review);
                when(userMeetingRepository.findByMeetingAndUser(any(), any(User.class))).thenReturn(Optional.of(UserMeeting.builder().user(receiver).build())).thenReturn(Optional.of(UserMeeting.builder().user(user).build()));
                when(reviewRepository.findByMeetingAndReceiverAndReviewer(any(), any(), any())).thenReturn(Optional.empty());

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
                when(userRepository.findById(any())).thenReturn(Optional.empty());
                Exception e = assertThrows(NotFoundException.class, () -> reviewService.createReview(user, reviewRequestDto));
                Assertions.assertThat(e.getMessage()).isEqualTo("Not Found Review Receiver User with id 2");
            }

            @Test
            @DisplayName("[API][Service] 모임의 ID가 존재하지 않은 경우")
            void createReviewFail2() {
                when(userRepository.findById(any())).thenReturn(Optional.of(user));
                when(meetingRepository.findById(any())).thenReturn(Optional.empty());

                Exception e = assertThrows(NotFoundException.class, () -> reviewService.createReview(user, reviewRequestDto));
                Assertions.assertThat(e.getMessage()).isEqualTo("Not Found the Meeting with id 3");
            }

            @Test
            @DisplayName("[API][Service] 리뷰를 받는 유저가 모임에 참여하지 않은 경우")
            void createReviewFail3() {
                when(userRepository.findById(any())).thenReturn(Optional.of(receiver));
                when(meetingRepository.findById(any())).thenReturn(Optional.of(meeting));
                when(userMeetingRepository.findByMeetingAndUser(meeting, receiver)).thenReturn(Optional.empty());

                Exception e = assertThrows(BusinessException.class, () -> reviewService.createReview(user, reviewRequestDto));
                Assertions.assertThat(e.getMessage()).isEqualTo("Who received the review did not participate meeting");
            }

            @Test
            @DisplayName("[API][Service] 리뷰를 작성하는 유저가 모임에 참여하지 않은 경우")
            void createReviewFail4() {
                when(userRepository.findById(any())).thenReturn(Optional.of(receiver));
                when(meetingRepository.findById(any())).thenReturn(Optional.of(meeting));
                UserMeeting userMeeting = UserMeeting.builder().user(receiver).build();
                when(userMeetingRepository.findByMeetingAndUser(any(), any(User.class))).thenReturn(Optional.of(userMeeting)).thenReturn(Optional.empty());

                Exception e = assertThrows(BusinessException.class, () -> reviewService.createReview(user, reviewRequestDto));
                Assertions.assertThat(e.getMessage()).isEqualTo("Writer did not participate meeting");

            }

            @Test
            @DisplayName("[API][Service] 리뷰를 작성하는 유저가 리뷰를 받는 유저와 같은 경우")
            void createReviewFail5() {
                when(userRepository.findById(any())).thenReturn(Optional.of(user));
                when(meetingRepository.findById(any())).thenReturn(Optional.of(meeting));
                when(userMeetingRepository.findByMeetingAndUser(any(), any())).thenReturn(Optional.of(UserMeeting.builder().user(user).build()));

                Exception e = assertThrows(BusinessException.class, () -> reviewService.createReview(user, reviewRequestDto));
                Assertions.assertThat(e.getMessage()).isEqualTo("Can't Review Your Self");

            }

            @Test
            @DisplayName("[API][Service] 리뷰를 작성하는 유저가 이미 리뷰를 작성한 경우")
            void createReviewFail6() {
                when(userRepository.findById(any())).thenReturn(Optional.of(receiver));
                when(meetingRepository.findById(any())).thenReturn(Optional.of(meeting));
                when(userMeetingRepository.findByMeetingAndUser(any(), any(User.class))).thenReturn(Optional.of(UserMeeting.builder().user(receiver).build())).thenReturn(Optional.of(UserMeeting.builder().user(user).build()));
                when(reviewRepository.findByMeetingAndReceiverAndReviewer(any(), any(), any())).thenReturn(Optional.of(review));


                Exception e = assertThrows(BusinessException.class, () -> reviewService.createReview(user, reviewRequestDto));
                Assertions.assertThat(e.getMessage()).isEqualTo("Review Already Written");
            }
        }
    }


    @Nested
    @DisplayName("[API][Service] 받은 리뷰 전체 조회 테스트")
    class FindAllReceivedReviews {

        @Nested
        @DisplayName("[API][Service] 받은 리뷰 전체 조회 성공 테스트")
        class Success{

            @Test
            @DisplayName("[API][Service] 받은 리뷰 전체 조회 성공 테스트")
            void findAllReceivedReviews(){
                User user = User.builder().id(1L).build();
                User sender = User.builder().id(2L).build();
                Meeting meeting = Meeting.builder().id(3L).build();
                Review review = Review.builder()
                        .id(1L)
                        .comment("good")
                        .rating(5)
                        .reviewer(sender)
                        .receiver(user)
                        .meeting(meeting)
                        .build();

                when(reviewRepository.findByReceiver(user)).thenReturn(List.of(review));
                var result = reviewService.findAllReceivedReviews(user);
                Assertions.assertThat(result).isEqualTo(List.of(review));
            }
        }
    }

    @Nested
    @DisplayName("[API][Service] 작성 리뷰 전체 조회 테스트")
    class FindAllReviewerReviews {

        @Nested
        @DisplayName("[API][Service] 작성 리뷰 전체 조회 성공 테스트")
        class Success{

            @Test
            @DisplayName("[API][Service] 작성 리뷰 전체 조회 성공 테스트")
            void findAllReviewerReviews(){
                User user = User.builder().id(1L).build();
                User receiver = User.builder().id(2L).build();
                Meeting meeting = Meeting.builder().id(3L).build();
                Review review = Review.builder()
                        .id(1L)
                        .comment("good")
                        .rating(5)
                        .reviewer(user)
                        .receiver(receiver)
                        .meeting(meeting)
                        .build();

                when(reviewRepository.findByReviewer(user)).thenReturn(List.of(review));
                var result = reviewService.findAllReviewerReviews(user);
                Assertions.assertThat(result).isEqualTo(List.of(review));
            }
        }
    }

    @Nested
    @DisplayName("[API][Service] 작성 리류 삭제 테스트")
    class DeleteReview{
        @Nested
        @DisplayName("[API][Service] 작성 리뷰 삭제 성공 테스트")
        class Success {
            @Test
            @DisplayName("[API][Service] 리뷰 작성자 삭제 성공 테스트")
            void deleteReview(){
                User user = User.builder().id(1L).build();
                User receiver = User.builder().id(2L).build();
                Meeting meeting = Meeting.builder().id(3L).build();
                Review review = Review.builder()
                        .id(1L)
                        .comment("good")
                        .rating(5)
                        .reviewer(user)
                        .receiver(receiver)
                        .isReviewerHidden(false)
                        .isReceiverHidden(false)
                        .meeting(meeting)
                        .build();

                when(reviewRepository.findById(any())).thenReturn(Optional.of(review));
                when(reviewRepository.save(any(Review.class))).thenReturn(review);

                reviewService.deleteReview(user, 1L);

                Assertions.assertThat(review.getIsReviewerHidden()).isEqualTo(true);
                Assertions.assertThat(review.getIsReceiverHidden()).isEqualTo(false);
            }


            @Test
            @DisplayName("[API][Service] 리뷰 받은 사람 삭제 성공 테스트")
            void deleteReview2(){
                User user = User.builder().id(1L).build();
                User writer = User.builder().id(2L).build();
                Meeting meeting = Meeting.builder().id(3L).build();
                Review review = Review.builder()
                        .id(1L)
                        .comment("good")
                        .rating(5)
                        .reviewer(writer)
                        .receiver(user)
                        .isReviewerHidden(false)
                        .isReceiverHidden(false)
                        .meeting(meeting)
                        .build();

                when(reviewRepository.findById(any())).thenReturn(Optional.of(review));
                when(reviewRepository.save(any(Review.class))).thenReturn(review);

                reviewService.deleteReview(user, 1L);

                Assertions.assertThat(review.getIsReviewerHidden()).isEqualTo(false);
                Assertions.assertThat(review.getIsReceiverHidden()).isEqualTo(true);



            }
        }

    }


}
