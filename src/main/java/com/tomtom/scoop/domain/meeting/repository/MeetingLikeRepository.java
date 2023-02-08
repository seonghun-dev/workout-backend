package com.tomtom.scoop.domain.meeting.repository;

import com.tomtom.scoop.domain.meeting.model.entity.Meeting;
import com.tomtom.scoop.domain.meeting.model.entity.MeetingLike;
import com.tomtom.scoop.domain.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MeetingLikeRepository extends JpaRepository<MeetingLike, Long> {
    Optional<MeetingLike> findByMeetingAndUser(Meeting meeting, User user);
}