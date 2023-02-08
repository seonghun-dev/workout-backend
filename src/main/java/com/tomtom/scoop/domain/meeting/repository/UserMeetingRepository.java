package com.tomtom.scoop.domain.meeting.repository;

import com.tomtom.scoop.domain.meeting.model.entity.Meeting;
import com.tomtom.scoop.domain.meeting.model.entity.UserMeeting;
import com.tomtom.scoop.domain.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserMeetingRepository extends JpaRepository<UserMeeting, Long> {
    List<UserMeeting> findByUser(User user);

    Optional<UserMeeting> findByMeetingAndUser(Meeting meeting, User user);

}