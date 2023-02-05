package com.tomtom.scoop.domain.meeting.service;

import com.tomtom.scoop.domain.meeting.model.dto.MeetingDto;
import com.tomtom.scoop.domain.meeting.model.dto.request.MeetingRequestDto;
import com.tomtom.scoop.domain.meeting.model.dto.response.MeetingDetailResponseDto;
import com.tomtom.scoop.domain.meeting.model.dto.response.MeetingListResponseDto;
import com.tomtom.scoop.domain.meeting.model.entity.*;
import com.tomtom.scoop.domain.meeting.repository.MeetingLocationRepository;
import com.tomtom.scoop.domain.meeting.repository.MeetingRepository;
import com.tomtom.scoop.domain.meeting.repository.MeetingTypeRepository;
import com.tomtom.scoop.domain.meeting.repository.UserMeetingRepository;
import com.tomtom.scoop.domain.user.model.entity.Exercise;
import com.tomtom.scoop.domain.user.model.entity.ExerciseLevel;
import com.tomtom.scoop.domain.user.model.entity.User;
import com.tomtom.scoop.domain.user.repository.ExerciseLevelRepository;
import com.tomtom.scoop.domain.user.repository.ExerciseRepository;
import com.tomtom.scoop.domain.user.repository.UserRepository;
import com.tomtom.scoop.global.exception.CustomException;
import com.tomtom.scoop.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MeetingService {
    private final MeetingRepository meetingRepository;
    private final UserMeetingRepository userMeetingRepository;
    private final MeetingLocationRepository meetingLocationRepository;
    private final MeetingTypeRepository meetingTypeRepository;
    private final ExerciseLevelRepository exerciseLevelRepository;
    private final ExerciseRepository exerciseRepository;
    private final UserRepository userRepository;

    public MeetingDto.response createMeeting(MeetingRequestDto meetingDto) {
        MeetingType meetingType = meetingTypeRepository.findByName(meetingDto.getMeetingType())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        Exercise exercise = exerciseRepository.findByName(meetingDto.getExerciseName())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));


        ExerciseLevel exerciseLevel = exerciseLevelRepository.findByLevelAndExerciseId(meetingDto.getExerciseLevel(), exercise.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        LocalDateTime today = LocalDateTime.now();
        GeometryFactory gf = new GeometryFactory();
        Point point = gf.createPoint(new Coordinate(meetingDto.getLocationLatitude(), meetingDto.getLocationLongitude()));

        MeetingLocation meetingLocation = MeetingLocation.builder()
                .locationName(meetingDto.getLocationName())
                .locationDetail(meetingDto.getLocationDetail())
                .city(meetingDto.getLocationCity())
                .location(point)
                .build();


        Meeting meeting = Meeting.builder()
                .title(meetingDto.getTitle())
                .content(meetingDto.getContent())
                .user(null)
                .memberLimit(meetingDto.getMemberLimit())
                .eventDate(today)
                .viewCount(0)
                .exerciseLevel(exerciseLevel)
                .meetingLocation(meetingLocation)
                .meetingType(meetingType)
                .gender(meetingDto.getGender()).build();


        // add user to user meeting
        UserMeeting userMeeting = UserMeeting.builder()
                .meeting(meeting)
                .user(null)
                .status(MeetingStatus.OWNER)
                .build();

        meetingLocationRepository.save(meetingLocation);
        meetingRepository.save(meeting);
        userMeetingRepository.save(userMeeting);

        return new MeetingDto.response(
                meeting.getId(),
                meeting.getTitle(),
                meeting.getMemberLimit(),
                meeting.getContent(),
                meeting.getGender(),
                meeting.getImgUrl(),
                meeting.getEventDate(),
                meeting.getCreatedAt()
        );
    }

    public MeetingDetailResponseDto findMeetingById(Long id) {
        Meeting meeting = meetingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 그룹이 없습니다. id=" + id));

        return MeetingDetailResponseDto.builder()
                .id(meeting.getId())
                .title(meeting.getTitle())
                .content(meeting.getContent())
                .memberLimit(meeting.getMemberLimit())
                .memberCount(meeting.getUserMeetings().size())
                .ownerName(meeting.getUser().getName())
                .ownerProfile(meeting.getUser().getProfileImg())
                .meetingType(meeting.getMeetingType().getName())
                .isLiked(false)
                .meetingUserProfiles(
                        meeting.getUserMeetings().stream().map(
                                userMeeting ->
                                        userMeeting.getUser().getProfileImg()
                        ).toList())
                .exerciseLevel(meeting.getExerciseLevel().getLevel())
                .build();
    }

    public MeetingDto.response deleteMeeting(Long id) {
        Meeting meeting = meetingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 그룹이 없습니다. id=" + id));
        meeting.setDeleted(true);
        meetingRepository.save(meeting);
        return new MeetingDto.response();
    }

    public MeetingDto.response updateMeeting(Long id, MeetingDto.request meetingDto) {
        Meeting meeting = meetingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 그룹이 없습니다. id=" + id));
        meetingRepository.save(meeting);
        return new MeetingDto.response(
                meeting.getId(),
                meeting.getTitle(),
                meeting.getMemberLimit(),
                meeting.getContent(),
                meeting.getGender(),
                meeting.getImgUrl(),
                meeting.getEventDate(),
                meeting.getCreatedAt()
        );
    }


    public List<MeetingListResponseDto> findAllMeetings(Pageable pageable) {
        List<Meeting> meetings = meetingRepository.findByEventDateGreaterThanOrderByEventDateAsc(LocalDateTime.now(), pageable);
        return meetings.stream().map(meeting ->
                MeetingListResponseDto
                        .builder()
                        .id(meeting.getId())
                        .title(meeting.getTitle())
                        .city(meeting.getMeetingLocation().getCity())
                        .eventDate(meeting.getEventDate())
                        .memberCount(meeting.getUserMeetings().size())
                        .memberLimit(meeting.getMemberLimit())
                        .exerciseName(meeting.getExerciseLevel().getExercise().getName())
                        .exerciseLevel(meeting.getExerciseLevel().getLevel())
                        .meetingType(meeting.getMeetingType().getName())
                        .imgUrl(meeting.getImgUrl())
                        .build()
        ).toList();
    }

    public List<MeetingListResponseDto> findUpcomingMeetingByUser(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 유저를 찾을 수 없습니다."));
        List<UserMeeting> userMeeting = userMeetingRepository.findByUser(user);

        return userMeeting.stream().map(
                meeting ->
                        MeetingListResponseDto
                                .builder()
                                .id(meeting.getId())
                                .title(meeting.getMeeting().getTitle())
                                .city(meeting.getMeeting().getMeetingLocation().getCity())
                                .eventDate(meeting.getMeeting().getEventDate())
                                .memberCount(meeting.getMeeting().getUserMeetings().size())
                                .memberLimit(meeting.getMeeting().getMemberLimit())
                                .exerciseName(meeting.getMeeting().getExerciseLevel().getExercise().getName())
                                .exerciseLevel(meeting.getMeeting().getExerciseLevel().getLevel())
                                .meetingType(meeting.getMeeting().getMeetingType().getName())
                                .imgUrl(meeting.getMeeting().getImgUrl())
                                .build()
        ).toList();
    }

    public List<MeetingListResponseDto> findPastMeetingByUser(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 유저를 찾을 수 없습니다."));
        List<UserMeeting> userMeeting = userMeetingRepository.findByUser(user);

        return userMeeting.stream().map(
                meeting ->
                        MeetingListResponseDto
                                .builder()
                                .id(meeting.getId())
                                .title(meeting.getMeeting().getTitle())
                                .city(meeting.getMeeting().getMeetingLocation().getCity())
                                .eventDate(meeting.getMeeting().getEventDate())
                                .memberCount(meeting.getMeeting().getUserMeetings().size())
                                .memberLimit(meeting.getMeeting().getMemberLimit())
                                .exerciseName(meeting.getMeeting().getExerciseLevel().getExercise().getName())
                                .exerciseLevel(meeting.getMeeting().getExerciseLevel().getLevel())
                                .meetingType(meeting.getMeeting().getMeetingType().getName())
                                .imgUrl(meeting.getMeeting().getImgUrl())
                                .build()
        ).toList();
    }

    public List<MeetingDto.response> findLikeMeetingByUser(Long userId) {
        // todo()!
        PageRequest pageRequest = PageRequest.of(0, 2);
        List<Meeting> meetings = meetingRepository.findByEventDateGreaterThanOrderByEventDateAsc(LocalDateTime.now(), pageRequest);
        return meetings.stream().map(meeting -> new MeetingDto.response(
                meeting.getId(),
                meeting.getTitle(),
                meeting.getMemberLimit(),
                meeting.getContent(),
                meeting.getGender(),
                meeting.getImgUrl(),
                meeting.getEventDate(),
                meeting.getCreatedAt()
        )).toList();
    }

    public List<MeetingDto.response> searchMeetingByKeyword(String keyword, Pageable pageable) {
        // todo()!
        List<Meeting> meetings = meetingRepository.findByEventDateGreaterThanOrderByEventDateAsc(LocalDateTime.now(), pageable);
        return meetings.stream().map(meeting -> new MeetingDto.response(
                meeting.getId(),
                meeting.getTitle(),
                meeting.getMemberLimit(),
                meeting.getContent(),
                meeting.getGender(),
                meeting.getImgUrl(),
                meeting.getEventDate(),
                meeting.getCreatedAt()
        )).toList();
    }

}
