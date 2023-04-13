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
import com.tomtom.scoop.global.exception.ErrorCode;
import com.tomtom.scoop.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final MeetingRepository meetingRepository;
    private final UserMeetingRepository userMeetingRepository;

    public Review createReview(User user, ReviewRequestDto reviewRequestDto) {
        User receiver = userRepository.findById(reviewRequestDto.getReceiverId()).orElseThrow(() -> new NotFoundException(ErrorCode.REVIEW_RECEIVER_USER_NOT_FOUNT, reviewRequestDto.getReceiverId()));
        Meeting meeting = meetingRepository.findById(reviewRequestDto.getMeetingId()).orElseThrow(() -> new NotFoundException(ErrorCode.MEETING_NOT_FOUND, reviewRequestDto.getMeetingId()));
        userMeetingRepository.findByMeetingAndUser(meeting, receiver).orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_RECEIVER_NOT_PARTICIPATE_MEETING));
        userMeetingRepository.findByMeetingAndUser(meeting, user).orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_WRITER_NOT_PARTICIPATE_MEETING));

        if (Objects.equals(user.getId(), receiver.getId())) {
            throw new BusinessException(ErrorCode.NOT_REVIEW_USER_SELF);
        }

        var exists = reviewRepository.findByMeetingAndReceiverAndReviewer(meeting, receiver, user);
        if (exists.isPresent()) {
            throw new BusinessException(ErrorCode.REVIEW_ALREADY_WRITTEN);
        }

        Review review = Review.builder().reviewer(user).receiver(receiver)
                .isReviewerHidden(false).isReviewerHidden(false)
                .comment(reviewRequestDto.getComment())
                .rating(reviewRequestDto.getRating())
                .meeting(meeting)
                .build();

        return reviewRepository.save(review);
    }

    public List<Review> findAllReceivedReviews(User user) {
        return reviewRepository.findByReceiver(user);
    }

    public List<Review> findAllReviewerReviews(User user) {
        return reviewRepository.findByReviewer(user);
    }

    public void deleteReview(User user, Long id) {
        Review review = reviewRepository.findById(id).orElseThrow(() -> new NotFoundException(ErrorCode.REVIEW_NOT_FOUND, id));
        if (review.getReviewer().equals(user)) {
            review.setIsReviewerHidden(true);
        } else if (review.getReceiver().equals(user)) {
            review.setIsReceiverHidden(true);
        } else {
            throw new BusinessException(ErrorCode.REVIEW_DELETE_AUTHORITY_NOT_MATCH);
        }
        reviewRepository.save(review);
    }

    public List<User> findAllReceivedUser(User user, Long meetingId) {
        Meeting meeting = meetingRepository.findById(meetingId).orElseThrow(() -> new NotFoundException(ErrorCode.MEETING_NOT_FOUND, meetingId));
        List<Review> reviews = reviewRepository.findByMeeting(meeting);
        List<User> doneUser = reviews.stream().map(Review::getReceiver).toList();
        return meeting.getUserMeetings().stream().map(UserMeeting::getUser).filter(userMeetingUser -> !doneUser.contains(userMeetingUser)).toList();
    }
}
