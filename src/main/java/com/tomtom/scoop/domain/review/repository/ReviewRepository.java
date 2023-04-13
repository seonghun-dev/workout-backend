package com.tomtom.scoop.domain.review.repository;

import com.tomtom.scoop.domain.meeting.model.entity.Meeting;
import com.tomtom.scoop.domain.review.model.entity.Review;
import com.tomtom.scoop.domain.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByReceiver(User receiver);

    List<Review> findByReviewer(User reviewer);

    List<Review> findByMeeting(Meeting meeting);

    Optional<Review> findByMeetingAndReceiverAndReviewer(Meeting meeting, User Receiver, User reviewer);
}