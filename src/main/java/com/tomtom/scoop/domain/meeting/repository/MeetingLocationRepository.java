package com.tomtom.scoop.domain.meeting.repository;

import com.tomtom.scoop.domain.meeting.model.entity.MeetingLocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingLocationRepository extends JpaRepository<MeetingLocation, Long> {
}