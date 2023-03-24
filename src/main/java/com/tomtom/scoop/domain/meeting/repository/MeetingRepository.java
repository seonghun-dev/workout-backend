package com.tomtom.scoop.domain.meeting.repository;

import com.tomtom.scoop.domain.meeting.model.entity.Meeting;
import com.tomtom.scoop.domain.user.model.entity.User;
import lombok.NonNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    @Query("select m from Meeting m join fetch m.meetingLocation join fetch m.exercise join fetch m.meetingType where m.eventDate > now() and m.isDeleted = false order by m.eventDate asc")
    List<Meeting> findByEventDateGreaterThanOrderByEventDateAsc();

    @Query("select m from Meeting m join fetch m.meetingLocation join fetch m.userMeetings join fetch m.exercise where m.id=?1")
    @NonNull()
    Optional<Meeting> findById(Long id);

    @Query("select m from Meeting m join fetch m.meetingLocation join fetch m.exerciseLevel  join fetch m.exercise join fetch m.meetingType where m.id in (select um.meeting from UserMeeting um where um.user = ?1 and (um.status = 'ACCEPTED' or um.status = 'OWNER')) and m.eventDate < ?2")
    List<Meeting> findUserPastMeeting(User user, LocalDateTime now, Pageable pageable);

    @Query("select m from Meeting m join fetch m.meetingLocation join fetch m.exerciseLevel join fetch m.exercise join fetch m.meetingType where m.id in (select um.meeting from UserMeeting um where um.user = ?1 and um.status = 'ACCEPTED') and m.eventDate > ?2")
    List<Meeting> findUserNextMeeting(User user, LocalDateTime now, Pageable pageable);

    @Query("select m from Meeting m join fetch m.meetingLocation join fetch m.exerciseLevel join fetch m.exercise join fetch m.meetingType where m.id in (select um.meeting from UserMeeting um where um.user = ?1 and um.status = 'WAITING') and m.eventDate > ?2")
    List<Meeting> findUserWaitingMeeting(User user, LocalDateTime now, Pageable pageable);

    @Query("select m from Meeting m join fetch m.meetingLocation join fetch m.userMeetings join fetch m.exercise where m.user = ?1")
    List<Meeting> findUserOwnedMeeting(User user, Pageable pageable);

    @Query("select m from Meeting m join fetch m.meetingLocation join fetch m.exerciseLevel join fetch m.exercise join fetch m.meetingType where m.id in (select ml.meeting from MeetingLike ml where ml.user = ?1)")
    List<Meeting> findUserLikedMeeting(User user, Pageable pageable);

    Optional<Meeting> findByUserAndId(User user, Long meetingId);

    List<Meeting> findByTitleContainingOrContentContainingAndEventDateGreaterThan(String title, String content, LocalDateTime now, Pageable pageable);
}
