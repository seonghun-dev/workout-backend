package com.tomtom.scoop.domain.meeting.repository;

import com.tomtom.scoop.domain.meeting.model.entity.MeetingLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingLikeRepository extends JpaRepository<MeetingLike, Long> {
}