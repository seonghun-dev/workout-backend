package com.tomtom.scoop.domain.meeting.repository;

import com.tomtom.scoop.domain.meeting.model.entity.MeetingType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MeetingTypeRepository extends JpaRepository<MeetingType, Long> {
    Optional<MeetingType> findByName(String meetingType);
}