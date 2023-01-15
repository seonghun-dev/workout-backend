package com.tomtom.scoop.domain.meeting.repository;

import com.tomtom.scoop.domain.meeting.model.entity.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {
}
