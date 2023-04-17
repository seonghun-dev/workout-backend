package com.tomtom.scoop.domain.meeting.service;

import com.tomtom.scoop.domain.exercise.model.entity.ExerciseLevel;
import com.tomtom.scoop.domain.exercise.repository.ExerciseLevelRepository;
import com.tomtom.scoop.domain.meeting.model.dto.request.FindAllMeetingRequestDto;
import com.tomtom.scoop.domain.meeting.model.dto.request.MeetingRequestDto;
import com.tomtom.scoop.domain.meeting.model.dto.response.MeetingDetailResponseDto;
import com.tomtom.scoop.domain.meeting.model.dto.response.MeetingImageResponseDto;
import com.tomtom.scoop.domain.meeting.model.dto.response.MeetingResponseDto;
import com.tomtom.scoop.domain.meeting.model.entity.*;
import com.tomtom.scoop.domain.meeting.repository.*;
import com.tomtom.scoop.domain.notification.event.JoinMeetingEvent;
import com.tomtom.scoop.domain.notification.event.JoinMeetingResultEvent;
import com.tomtom.scoop.domain.user.model.entity.User;
import com.tomtom.scoop.domain.user.repository.UserRepository;
import com.tomtom.scoop.global.exception.BusinessException;
import com.tomtom.scoop.global.exception.ErrorCode;
import com.tomtom.scoop.global.exception.NotFoundException;
import com.tomtom.scoop.infrastructor.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MeetingService {
    private final ApplicationEventPublisher eventPublisher;
    private final MeetingRepository meetingRepository;
    private final UserMeetingRepository userMeetingRepository;
    private final MeetingLocationRepository meetingLocationRepository;
    private final MeetingTypeRepository meetingTypeRepository;
    private final UserRepository userRepository;
    private final MeetingLikeRepository meetingLikeRepository;
    private final ExerciseLevelRepository exerciseLevelRepository;

    private final S3Service s3Service;

    @Transactional
    public MeetingDetailResponseDto createMeeting(User user, MeetingRequestDto meetingDto) {

        final Integer DEFAULT_VIEW_COUNT = 0;
        final Integer DEFAULT_MEMBER_COUNT = 1;

        MeetingType meetingType = meetingTypeRepository.findByName(meetingDto.getMeetingType())
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEETING_TYPE_NOT_FOUND, meetingDto.getMeetingType()));

        ExerciseLevel exerciseLevel = exerciseLevelRepository.findById(meetingDto.getExerciseLevel())
                .orElseThrow(() -> new NotFoundException(ErrorCode.EXERCISE_LEVEL_NOT_FOUND, meetingDto.getExerciseLevel()));


        LocalDateTime today = meetingDto.getMeetingDate();


        GeometryFactory gf = new GeometryFactory();
        Point point = gf.createPoint(new Coordinate(meetingDto.getLocationLatitude(), meetingDto.getLocationLongitude()));
        MeetingLocation meetingLocation = MeetingLocation.builder()
                .locationName(meetingDto.getLocationName())
                .locationDetail(meetingDto.getLocationDetail())
                .city(meetingDto.getLocationCity())
                .location(point)
                .build();

        meetingLocationRepository.save(meetingLocation);

        Meeting meeting = Meeting.builder()
                .title(meetingDto.getTitle())
                .content(meetingDto.getContent())
                .user(user)
                .memberCount(DEFAULT_MEMBER_COUNT)
                .memberLimit(meetingDto.getMemberLimit())
                .eventDate(today)
                .viewCount(DEFAULT_VIEW_COUNT)
                .exerciseLevel(exerciseLevel)
                .meetingLocation(meetingLocation)
                .meetingType(meetingType)
                .gender(meetingDto.getGender()).build();


        UserMeeting userMeeting = UserMeeting.builder()
                .meeting(meeting)
                .user(user)
                .status(MeetingStatus.OWNER)
                .build();

        userMeetingRepository.save(userMeeting);

        meeting.setUserMeetings(List.of(userMeeting));
        meetingRepository.save(meeting);


        return MeetingDetailResponseDto.fromEntity(meeting);
    }

    public MeetingDetailResponseDto findMeetingById(Long id) {
        Meeting meeting = meetingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEETING_NOT_FOUND, id));

        return MeetingDetailResponseDto.fromEntity(meeting);
    }

    public void deleteMeeting(User user, Long id) {
        Meeting meeting = meetingRepository.findByUserAndId(user, id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEETING_NOT_FOUND, id));
        meeting.setDeleted(true);

        meetingRepository.save(meeting);
    }

    public MeetingDetailResponseDto joinMeeting(User user, Long id) {
        Meeting meeting = meetingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEETING_NOT_FOUND, id));

        userMeetingRepository.findByMeetingAndUser(meeting, user).ifPresent(userMeeting -> {
            throw new BusinessException(ErrorCode.ALREADY_JOINED_MEETING);
        });

        UserMeeting userMeeting = UserMeeting.builder()
                .meeting(meeting)
                .user(user)
                .status(MeetingStatus.WAITING)
                .build();

        JoinMeetingEvent joinMeetingEvent = JoinMeetingEvent.builder()
                .owner(meeting.getUser())
                .joinedUser(user)
                .meeting(meeting)
                .build();

        userMeetingRepository.save(userMeeting);

        eventPublisher.publishEvent(joinMeetingEvent);

        return null;
    }

    public MeetingDetailResponseDto leaveMeeting(User user, Long id) {
        Meeting meeting = meetingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEETING_NOT_FOUND, id));

        UserMeeting userMeeting = userMeetingRepository.findByMeetingAndUser(meeting, user)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEETING_NOT_FOUND, id));
        if (userMeeting.getStatus() == MeetingStatus.OWNER) {
            throw new BusinessException(ErrorCode.OWNER_CANNOT_LEAVE_MEETING);
        }
        if (userMeeting.getStatus() == MeetingStatus.REJECTED) {
            throw new BusinessException(ErrorCode.REJECTED_USER_CANNOT_LEAVE_MEETING);
        }
        userMeetingRepository.delete(userMeeting);

        return null;
    }

    public List<MeetingResponseDto> findAllMeetings(FindAllMeetingRequestDto findAllMeetingRequestDto) {
        return meetingRepository.findByEventDateGreaterThanOrderByEventDateAsc().stream()
                .map(MeetingResponseDto::fromEntity).toList();
    }

    public List<MeetingResponseDto> findUserUpcomingMeeting(User user, Pageable pageable) {
        return meetingRepository.findUserNextMeeting(user, LocalDateTime.now(), pageable).stream()
                .map(MeetingResponseDto::fromEntity).toList();
    }

    public List<MeetingResponseDto> findUserPastMeeting(User user, Pageable pageable) {
        return meetingRepository.findUserPastMeeting(user, LocalDateTime.now(), pageable).stream()
                .map(MeetingResponseDto::fromEntity).toList();
    }

    public List<MeetingResponseDto> findUserWaitingMeeting(User user, Pageable pageable) {
        return meetingRepository.findUserWaitingMeeting(user, LocalDateTime.now(), pageable).stream()
                .map(MeetingResponseDto::fromEntity).toList();
    }

    public List<MeetingResponseDto> findLikeMeetingByUser(User user, Pageable pageable) {
        return meetingRepository.findUserLikedMeeting(user, pageable).stream()
                .map(MeetingResponseDto::fromEntity).toList();
    }

    public MeetingDetailResponseDto likeMeeting(User user, Long meetingId) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEETING_NOT_FOUND, meetingId));
        MeetingLike meetingLike = MeetingLike.builder()
                .meeting(meeting)
                .user(user)
                .build();
        meetingLikeRepository.save(meetingLike);
        return null;
    }

    public MeetingDetailResponseDto unLikeMeeting(User user, Long meetingId) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEETING_NOT_FOUND, meetingId));
        MeetingLike meetingLike = meetingLikeRepository.findByMeetingAndUser(meeting, user).orElseThrow(() -> new BusinessException(ErrorCode.NOT_LIKED_MEETING));
        meetingLikeRepository.delete(meetingLike);
        return null;
    }

    public List<MeetingResponseDto> searchMeetingByKeyword(String keyword, Pageable pageable) {
        return meetingRepository.findByTitleContainingOrContentContainingAndEventDateGreaterThan(keyword, keyword, LocalDateTime.now(), pageable).stream()
                .map(MeetingResponseDto::fromEntity).toList();
    }


    public MeetingDetailResponseDto acceptMeeting(User user, Long id, Long requestUserId) {
        User requestUser = getUser(requestUserId);
        Meeting meeting = meetingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEETING_NOT_FOUND, id));

        if (meeting.getUser() != user) {
            throw new BusinessException(ErrorCode.NOT_MEETING_OWNER);
        }

        UserMeeting userMeeting = userMeetingRepository.findByMeetingAndUser(meeting, requestUser)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_JOINED_USER_IN_MEETING, requestUserId));

        if (userMeeting.getStatus() == MeetingStatus.ACCEPTED || userMeeting.getStatus() == MeetingStatus.OWNER) {
            throw new BusinessException(ErrorCode.ALREADY_ACCEPTED_USER_IN_MEETING);
        }


        userMeeting.setStatus(MeetingStatus.ACCEPTED);
        meeting.setMemberCount(meeting.getMemberCount() + 1);
        userMeetingRepository.save(userMeeting);
        meetingRepository.save(meeting);
        eventPublisher.publishEvent(new JoinMeetingResultEvent(meeting, user, MeetingStatus.ACCEPTED));
        return null;
    }


    public MeetingDetailResponseDto rejectMeeting(User user, Long id, Long requestUserId) {
        User requestUser = getUser(requestUserId);
        Meeting meeting = meetingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEETING_NOT_FOUND, id));

        if (meeting.getUser() != user) {
            throw new BusinessException(ErrorCode.NOT_MEETING_OWNER);
        }

        UserMeeting userMeeting = userMeetingRepository.findByMeetingAndUser(meeting, requestUser)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_JOINED_USER_IN_MEETING, requestUserId));

        if (userMeeting.getStatus() == MeetingStatus.ACCEPTED) {
            meeting.setMemberCount(meeting.getMemberCount() - 1);
            meetingRepository.save(meeting);
        }

        if (userMeeting.getStatus() == MeetingStatus.OWNER) {
            throw new BusinessException(ErrorCode.OWNER_CANNOT_REJECT_MEETING);
        }

        userMeeting.setStatus(MeetingStatus.REJECTED);
        userMeetingRepository.save(userMeeting);
        eventPublisher.publishEvent(new JoinMeetingResultEvent(meeting, user, MeetingStatus.REJECTED));
        return null;
    }

    public List<MeetingResponseDto> findOwnerMeetingByUser(User user, Pageable pageable) {
        return meetingRepository.findUserOwnedMeeting(user, pageable).stream()
                .map(MeetingResponseDto::fromEntity).toList();
    }


    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND, userId));
    }

    public MeetingImageResponseDto uploadMeetingImage(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException(ErrorCode.FILE_NOT_FOUND);
        }
        try {
            var result = s3Service.upload(file, "meeting");
            return new MeetingImageResponseDto(result);
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.FILE_UPLOAD_ERROR);
        }
    }
}
