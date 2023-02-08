package com.tomtom.scoop.domain.review.service;

import com.tomtom.scoop.domain.meeting.model.entity.Meeting;
import com.tomtom.scoop.domain.meeting.model.entity.UserMeeting;
import com.tomtom.scoop.domain.meeting.repository.MeetingRepository;
import com.tomtom.scoop.domain.review.model.dto.request.ReviewRequestDto;
import com.tomtom.scoop.domain.review.model.entity.Review;
import com.tomtom.scoop.domain.review.repository.ReviewRepository;
import com.tomtom.scoop.domain.user.model.entity.User;
import com.tomtom.scoop.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final MeetingRepository meetingRepository;

    public Review createReview(User user, ReviewRequestDto reviewRequestDto) {
        User receiver = userRepository.findById(reviewRequestDto.getReceiverId()).orElseThrow(() -> new NotFoundException("User not found"));
        Meeting meeting = meetingRepository.findById(reviewRequestDto.getMeetingId()).orElseThrow(() -> new NotFoundException("Meeting not found"));
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
        Review review = reviewRepository.findById(id).orElseThrow(() -> new NotFoundException("Review not found"));
        if (review.getReviewer().equals(user)) {
            review.setIsReviewerHidden(true);
        } else if (review.getReceiver().equals(user)) {
            review.setIsReceiverHidden(true);
        } else {
            throw new NotFoundException("Review not found");
        }
        reviewRepository.save(review);
    }

    public List<User> findAllReceivedUser(User user, Long meetingId) {
        Meeting meeting = meetingRepository.findById(meetingId).orElseThrow(() -> new NotFoundException("Meeting not found"));
        List<Review> reviews = reviewRepository.findByMeeting(meeting);
        List<User> doneUser = reviews.stream().map(Review::getReceiver).toList();
        return meeting.getUserMeetings().stream().map(UserMeeting::getUser).filter(userMeetingUser -> !doneUser.contains(userMeetingUser)).toList();
    }
}
