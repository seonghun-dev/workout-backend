package com.tomtom.scoop.domain.meeting.repository;

import com.tomtom.scoop.domain.meeting.model.entity.Meeting;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    @Query("select m, count(m) from Meeting m join fetch m.meetingLocation join fetch m.exerciseLevel.exercise join fetch m.meetingType join fetch m.userMeetings where m.eventDate > ?1 and m.isDeleted = false order by m.eventDate asc ")
    List<Meeting> findByEventDateGreaterThanOrderByEventDateAsc(LocalDateTime now, Pageable pageable);


    @Query("select m from Meeting m join fetch m.meetingLocation join fetch m.userMeetings join fetch m.exerciseLevel where m.id=?1")
    Optional<Meeting> findById(Long id);

}
