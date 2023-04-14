package com.tomtom.scoop.domain.meeting.service;

import com.tomtom.scoop.domain.common.Gender;
import com.tomtom.scoop.domain.exercise.model.entity.ExerciseLevel;
import com.tomtom.scoop.domain.exercise.repository.ExerciseLevelRepository;
import com.tomtom.scoop.domain.meeting.model.dto.request.FindAllMeetingRequestDto;
import com.tomtom.scoop.domain.meeting.model.dto.request.MeetingRequestDto;
import com.tomtom.scoop.domain.meeting.model.dto.response.MeetingListResponseDto;
import com.tomtom.scoop.domain.meeting.model.entity.*;
import com.tomtom.scoop.domain.meeting.repository.*;
import com.tomtom.scoop.domain.user.model.entity.User;
import com.tomtom.scoop.domain.user.repository.UserRepository;
import com.tomtom.scoop.global.exception.BusinessException;
import com.tomtom.scoop.global.exception.NotFoundException;
import com.tomtom.scoop.infrastructor.s3.S3Service;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("[API][Service] 모임 관련 테스트")
public class MeetingServiceTest {

    @Mock
    MeetingRepository meetingRepository;
    @Mock
    UserMeetingRepository userMeetingRepository;
    @Mock
    MeetingLocationRepository meetingLocationRepository;
    @Mock
    MeetingTypeRepository meetingTypeRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    MeetingLikeRepository meetingLikeRepository;
    @Mock
    ExerciseLevelRepository exerciseLevelRepository;
    @Mock
    ApplicationEventPublisher eventPublisher;
    @Mock
    S3Service s3Service;

    @InjectMocks
    private MeetingService meetingService;

    @Nested
    @DisplayName("[API][Service] 모임 생성 테스트")
    class CreateMeeting {

        private User user;
        private MeetingRequestDto meetingRequestDto;
        private MeetingType meetingType;
        private Meeting meeting;

        private MeetingLocation meetingLocation;

        private UserMeeting userMeeting;

        private ExerciseLevel exerciseLevel;

        @BeforeEach
        void setUp() {
            user = User.builder()
                    .id(1L)
                    .name("홍길동")
                    .profileImg("https://api.scoop.com/user/profile/1.png")
                    .build();

            meetingRequestDto = MeetingRequestDto.builder()
                    .title("모임 제목")
                    .content("모임 내용")
                    .memberLimit(10)
                    .gender(Gender.MALE)
                    .meetingDate(LocalDateTime.now())
                    .locationLatitude(37.123456f)
                    .locationLongitude(127.123456f)
                    .locationName("KonKuk University")
                    .locationDetail("Library")
                    .locationCity("Seoul")
                    .exerciseLevel(1L)
                    .meetingType("Play")
                    .build();

            meetingType = new MeetingType(1L, "Play");

            exerciseLevel = new ExerciseLevel(1L, "Running", "Beginner");

            GeometryFactory gf = new GeometryFactory();
            Point point = gf.createPoint(new Coordinate(meetingRequestDto.getLocationLatitude(), meetingRequestDto.getLocationLongitude()));


            meetingLocation = MeetingLocation.builder()
                    .location(point)
                    .locationName(meetingRequestDto.getLocationName())
                    .locationDetail(meetingRequestDto.getLocationDetail())
                    .city(meetingRequestDto.getLocationCity())
                    .build();

            meeting = Meeting.builder()
                    .id(1L)
                    .title(meetingRequestDto.getTitle())
                    .content(meetingRequestDto.getContent())
                    .user(user)
                    .memberCount(1)
                    .viewCount(0)
                    .memberLimit(meetingRequestDto.getMemberLimit())
                    .eventDate(meetingRequestDto.getMeetingDate())
                    .exerciseLevel(exerciseLevel)
                    .meetingLocation(meetingLocation)
                    .meetingType(meetingType)
                    .gender(meetingRequestDto.getGender())
                    .build();


            userMeeting = UserMeeting.builder()
                    .user(user)
                    .meeting(meeting)
                    .status(MeetingStatus.OWNER)
                    .build();


        }

        @Nested
        @DisplayName("[API][Service] 모임 생성 성공 테스트")
        class Success {

            @Test
            @DisplayName("[API][Service] 모임 생성 성공 테스트")
            void createMeetingSuccess1() {

                when(meetingTypeRepository.findByName(any())).thenReturn(Optional.of(meetingType));
                when(exerciseLevelRepository.findById(any())).thenReturn(Optional.of(exerciseLevel));
                when(meetingLocationRepository.save(any(MeetingLocation.class))).thenReturn(meetingLocation);
                when(meetingRepository.save(any(Meeting.class))).thenReturn(meeting);
                when(userMeetingRepository.save(any(UserMeeting.class))).thenReturn(userMeeting);

                var result = meetingService.createMeeting(user, meetingRequestDto);


                Assertions.assertThat(result).isNotNull();
                Assertions.assertThat(result.getMeetingType()).isEqualTo("Play");
                Assertions.assertThat(result.getMemberCount()).isEqualTo(1);
                Assertions.assertThat(result.getMemberLimit()).isEqualTo(10);
                Assertions.assertThat(result.getExerciseLevel()).isEqualTo("Beginner");
                Assertions.assertThat(result.getOwnerName()).isEqualTo("홍길동");
                Assertions.assertThat(result.getOwnerProfile()).isEqualTo("https://api.scoop.com/user/profile/1.png");
                Assertions.assertThat(result.getIsLiked()).isFalse();
            }
        }

        @Nested
        @DisplayName("[API][Service] 모임 생성 실패 테스트")
        class Fail {
            @Test
            @DisplayName("[API][Service] DB에 존재하지 않는 모임 타입으로 모임 생성시 실패 테스트")
            void createMeetingFail1() {

                when(meetingTypeRepository.findByName(any())).thenReturn(Optional.empty());

                assertThrows(NotFoundException.class, () -> meetingService.createMeeting(user, meetingRequestDto));

            }

            @Test
            @DisplayName("[API][Service] DB에 존재하지 않는 운동 타입으로 모임 생성시 실패 테스트")
            void createMeetingFail2() {

                when(meetingTypeRepository.findByName(any())).thenReturn(Optional.of(meetingType));
                when(exerciseLevelRepository.findById(any())).thenReturn(Optional.empty());

                assertThrows(NotFoundException.class, () -> meetingService.createMeeting(user, meetingRequestDto));

            }
        }


    }

    @Nested
    @DisplayName("[API][Service] 모임 단건 조회 테스트")
    class FindMeetingOne {

        private Meeting meeting;

        private User createUser(Long id) {
            return User.builder()
                    .id(1L)
                    .name("홍길동")
                    .profileImg("https://api.scoop.com/user/profile/" + id + ".png")
                    .build();
        }

        private UserMeeting createUserMeeting(User user, MeetingStatus meetingStatus) {
            return UserMeeting.builder()
                    .user(user)
                    .meeting(meeting)
                    .status(meetingStatus)
                    .build();
        }

        @BeforeEach
        void setUp() {

            User owner = createUser(1L);
            User participantUser1 = createUser(2L);
            User participantUser2 = createUser(3L);
            User participantUser3 = createUser(4L);

            LocalDateTime today = LocalDateTime.now();

            ExerciseLevel exerciseLevel = new ExerciseLevel(1L, "Running", "Beginner");
            MeetingType meetingType = new MeetingType(1L, "Play");
            MeetingLocation meetingLocation = new MeetingLocation(1L, "KonKuk University", "Library", "Seoul", null);

            List<UserMeeting> userMeetings = new ArrayList<>();
            userMeetings.add(createUserMeeting(owner, MeetingStatus.OWNER));
            userMeetings.add(createUserMeeting(participantUser1, MeetingStatus.ACCEPTED));
            userMeetings.add(createUserMeeting(participantUser2, MeetingStatus.WAITING));
            userMeetings.add(createUserMeeting(participantUser3, MeetingStatus.REJECTED));

            meeting = Meeting.builder()
                    .id(1L)
                    .title("모임 제목")
                    .content("모임 내용")
                    .user(owner)
                    .memberCount(1)
                    .viewCount(0)
                    .memberLimit(10)
                    .eventDate(today)
                    .exerciseLevel(exerciseLevel)
                    .meetingLocation(meetingLocation)
                    .userMeetings(userMeetings)
                    .meetingType(meetingType)
                    .gender(Gender.MALE)
                    .build();
        }

        @Nested
        @DisplayName("[API][Service] 모임 단건 조회 성공 테스트")
        class Success {

            @Test
            @DisplayName("[API][Service] 모임 단건 조회 성공 테스트")
            void findMeetingOneSuccess1() {
                when(meetingRepository.findById(any())).thenReturn(Optional.of(meeting));

                var result = meetingService.findMeetingById(1L);

                Assertions.assertThat(result).isNotNull();
                Assertions.assertThat(result.getId()).isEqualTo(1L);
                Assertions.assertThat(result.getMeetingType()).isEqualTo("Play");
                Assertions.assertThat(result.getMemberCount()).isEqualTo(1);
                Assertions.assertThat(result.getMemberLimit()).isEqualTo(10);
                Assertions.assertThat(result.getExerciseLevel()).isEqualTo("Beginner");
                Assertions.assertThat(result.getOwnerName()).isEqualTo("홍길동");
                Assertions.assertThat(result.getOwnerProfile()).isEqualTo("https://api.scoop.com/user/profile/1.png");
                Assertions.assertThat(result.getIsLiked()).isFalse();
                Assertions.assertThat(result.getMeetingUserProfiles()).isEqualTo(
                        List.of("https://api.scoop.com/user/profile/1.png",
                                "https://api.scoop.com/user/profile/2.png")
                );

            }

            @Nested
            @DisplayName("[API][Service] 모임 단건 조회 실패 테스트")
            class Fail {

                @Test
                @DisplayName("[API][Service] DB에 존재하지 않는 모임 ID로 모임 단건 조회시 실패 테스트")
                void findMeetingOneFail1() {
                    when(meetingRepository.findById(any())).thenReturn(Optional.empty());

                    assertThrows(NotFoundException.class, () -> meetingService.findMeetingById(1L));
                }

            }
        }

    }


    @Nested
    @DisplayName("[API][Service] 모임 삭제 테스트")
    class DeleteMeeting {

        private User user;
        private Meeting meeting;

        @BeforeEach
        void setUp() {
            user = User.builder()
                    .id(1L)
                    .name("홍길동")
                    .profileImg("https://api.scoop.com/user/profile/1.png")
                    .build();

            meeting = Meeting.builder()
                    .id(1L)
                    .build();
        }

        @Nested
        @DisplayName("[API][Service] 모임 삭제 성공 테스트")
        class Success {

            @Test
            @DisplayName("[API][Service] 모임 삭제 성공 테스트")
            void deleteMeetingSuccess1() {
                when(meetingRepository.findByUserAndId(any(), any())).thenReturn(Optional.of(meeting));

                meetingService.deleteMeeting(user, 1L);

                Assertions.assertThat(meeting.isDeleted()).isTrue();
            }

        }

        @Nested
        @DisplayName("[API][Service] 모임 삭제 실패 테스트")
        class Fail {

            @Test
            @DisplayName("[API][Service] DB에 존재하지 않는 모임 ID로 모임 삭제시 실패 테스트")
            void deleteMeetingFail1() {
                when(meetingRepository.findByUserAndId(any(), any())).thenReturn(Optional.empty());

                assertThrows(NotFoundException.class, () -> meetingService.deleteMeeting(user, 1L));
            }

        }

    }


    @Nested
    @DisplayName("[API][Service] 모임 참여 테스트")
    class JoinMeeting {
        Meeting meeting;

        User owner;
        User participant;

        @BeforeEach
        void setUp() {
            owner = User.builder()
                    .id(1L)
                    .name("홍길동")
                    .profileImg("https://api.scoop.com/user/profile/1.png")
                    .build();

            participant = User.builder()
                    .id(2L)
                    .name("홍길순")
                    .profileImg("https://api.scoop.com/user/profile/2.png")
                    .build();

            meeting = Meeting.builder()
                    .id(1L)
                    .user(owner)
                    .memberCount(1)
                    .build();
        }

        @Nested
        @DisplayName("[API][Service] 모임 참여 성공 테스트")
        class Success {

            @Test
            @DisplayName("[API][Service] 모임 참여 성공 테스트")
            void joinMeetingSuccess1() {
                UserMeeting userMeeting = UserMeeting.builder()
                        .user(participant)
                        .meeting(meeting)
                        .status(MeetingStatus.WAITING)
                        .build();

                when(meetingRepository.findById(any())).thenReturn(Optional.of(meeting));
                when(userMeetingRepository.findByMeetingAndUser(any(), any())).thenReturn(Optional.empty());
                when(userMeetingRepository.save(any(UserMeeting.class))).thenReturn(userMeeting);
                meetingService.joinMeeting(participant, 1L);

                Assertions.assertThat(meeting.getMemberCount()).isEqualTo(1);
                verify(userMeetingRepository, times(1)).save(any(UserMeeting.class));

            }

        }

        @Nested
        @DisplayName("[API][Service] 모임 참여 실패 테스트")
        class Fail {

            @Test
            @DisplayName("[API][Service] 모임에 해당하는 ID가 존재하지 않을 때 실패 테스트")
            void joinMeetingFail1() {
                when(meetingRepository.findById(any())).thenReturn(Optional.empty());

                assertThrows(NotFoundException.class, () -> meetingService.joinMeeting(participant, 1L));
            }

            @Test
            @DisplayName("[API][Service] 이미 모임에 참여중인 경우 실패 테스트")
            void joinMeetingFail2() {
                UserMeeting userMeeting = UserMeeting.builder()
                        .user(participant)
                        .meeting(meeting)
                        .status(MeetingStatus.WAITING)
                        .build();

                when(meetingRepository.findById(any())).thenReturn(Optional.of(meeting));
                when(userMeetingRepository.findByMeetingAndUser(any(), any())).thenReturn(Optional.of(userMeeting));

                Exception e = assertThrows(BusinessException.class, () -> meetingService.joinMeeting(participant, 1L));
                Assertions.assertThat(e.getMessage()).isEqualTo("Already Joined the Meeting");
            }


        }


    }


    @Nested
    @DisplayName("[API][Service] 모임 탈퇴 테스트")
    class LeaveMeeting {

        private User owner;

        private User participantUser1;

        private Meeting meeting;

        private UserMeeting ownerMeeting;

        private UserMeeting userMeeting;

        @BeforeEach
        void setUp() {

            owner = User.builder()
                    .id(1L)
                    .name("홍길동")
                    .profileImg("https://api.scoop.com/user/profile/1.png")
                    .build();

            participantUser1 = User.builder()
                    .id(2L)
                    .name("홍길동")
                    .profileImg("https://api.scoop.com/user/profile/2.png")
                    .build();

            ownerMeeting = UserMeeting.builder()
                    .user(owner)
                    .meeting(meeting)
                    .status(MeetingStatus.OWNER)
                    .build();

            userMeeting = UserMeeting.builder()
                    .user(participantUser1)
                    .meeting(meeting)
                    .status(MeetingStatus.ACCEPTED)
                    .build();


            meeting = Meeting.builder()
                    .id(1L)
                    .build();

        }

        @Nested
        @DisplayName("[API][Service] 모임 탈퇴 성공 테스트")
        class Success {
            @Test
            @DisplayName("[API][Service] ACCEPTED 상태 유저 모임 탈퇴 성공 테스트")
            void leaveMeetingSuccess1() {
                when(meetingRepository.findById(any())).thenReturn(Optional.of(meeting));
                when(userMeetingRepository.findByMeetingAndUser(any(), any())).thenReturn(Optional.of(userMeeting));

                meetingService.leaveMeeting(participantUser1, 1L);
                verify(userMeetingRepository, times(1)).delete(any());

            }

            @Test
            @DisplayName("[API][Service] WAITING 상태 유저 모임 탈퇴 성공 테스트")
            void leaveMeetingSuccess2() {
                userMeeting.setStatus(MeetingStatus.WAITING);
                when(meetingRepository.findById(any())).thenReturn(Optional.of(meeting));
                when(userMeetingRepository.findByMeetingAndUser(any(), any())).thenReturn(Optional.of(userMeeting));

                meetingService.leaveMeeting(participantUser1, 1L);
                verify(userMeetingRepository, times(1)).delete(any());

            }

        }

        @Nested
        @DisplayName("[API][Service] 모임 탈퇴 실패 테스트")
        class Fail {

            @Test
            @DisplayName("[API][Service] OWNER 상태 유저 모임 탈퇴 실패 테스트")
            void leaveMeetingFail1() {
                when(meetingRepository.findById(any())).thenReturn(Optional.of(meeting));
                when(userMeetingRepository.findByMeetingAndUser(any(), any())).thenReturn(Optional.of(ownerMeeting));

                Exception e = assertThrows(BusinessException.class, () -> meetingService.leaveMeeting(owner, 1L));
                Assertions.assertThat(e.getMessage()).isEqualTo("Owner Cannot Leave the Meeting");

            }

            @Test
            @DisplayName("[API][Service] REJECT 상태 유저 모임 탈퇴 실패 테스트")
            void leaveMeetingFail2() {
                userMeeting.setStatus(MeetingStatus.REJECTED);
                when(meetingRepository.findById(any())).thenReturn(Optional.of(meeting));
                when(userMeetingRepository.findByMeetingAndUser(any(), any())).thenReturn(Optional.of(userMeeting));

                Exception e = assertThrows(BusinessException.class, () -> meetingService.leaveMeeting(participantUser1, 1L));
                Assertions.assertThat(e.getMessage()).isEqualTo("Rejected User Cannot Leave the Meeting");

            }


            @Test
            @DisplayName("[API][Service] DB에 존재하지 않는 모임 ID로 모임 탈퇴 실패 테스트")
            void leaveMeetingFail3() {
                when(meetingRepository.findById(any())).thenReturn(Optional.empty());

                assertThrows(NotFoundException.class, () -> meetingService.leaveMeeting(participantUser1, 1L));
            }

            @Test
            @DisplayName("[API][Service] DB에 존재하지 않는 유저모임 ID로 모임 탈퇴 실패 테스트")
            void leaveMeetingFail4() {
                when(meetingRepository.findById(any())).thenReturn(Optional.of(meeting));
                when(userMeetingRepository.findByMeetingAndUser(any(), any())).thenReturn(Optional.empty());

                assertThrows(NotFoundException.class, () -> meetingService.leaveMeeting(participantUser1, 1L));
            }
        }

    }

    @Nested
    @DisplayName("[API][Service] 전체 모임 조회 테스트")
    class FindAllMeeting {

        User user;
        List<Meeting> meetings = new ArrayList<>();

        Meeting meeting;

        FindAllMeetingRequestDto requestDto;

        @BeforeEach
        void setUp() {
            MeetingType meetingType = new MeetingType(1L, "Play");
            ExerciseLevel exerciseLevel = new ExerciseLevel(1L, "Running", "Beginner");

            GeometryFactory gf = new GeometryFactory();
            Point point = gf.createPoint(new Coordinate(1.3, 1.4));


            MeetingLocation meetingLocation = MeetingLocation.builder()
                    .location(point)
                    .locationName("Konkuk university")
                    .locationDetail("library")
                    .city("Seoul")
                    .build();

            UserMeeting userMeeting = UserMeeting.builder()
                    .user(user)
                    .meeting(meeting)
                    .status(MeetingStatus.OWNER)
                    .build();


            meeting = Meeting.builder()
                    .id(1L)
                    .title("모임 제목")
                    .content("모임 내용")
                    .user(user)
                    .memberCount(1)
                    .viewCount(0)
                    .memberLimit(10)
                    .eventDate(LocalDateTime.now())
                    .exerciseLevel(exerciseLevel)
                    .meetingLocation(meetingLocation)
                    .userMeetings(Collections.singletonList(userMeeting))
                    .meetingType(meetingType)
                    .gender(Gender.MALE)
                    .build();

            Meeting meeting1 = Meeting.builder()
                    .id(2L)
                    .title("모임 제목")
                    .content("모임 내용")
                    .user(user)
                    .memberCount(1)
                    .viewCount(0)
                    .memberLimit(10)
                    .eventDate(LocalDateTime.now())
                    .exerciseLevel(exerciseLevel)
                    .meetingLocation(meetingLocation)
                    .userMeetings(Collections.singletonList(userMeeting))
                    .meetingType(meetingType)
                    .gender(Gender.MALE)
                    .build();

            meetings.add(meeting);
            meetings.add(meeting1);
        }

        @Test
        @DisplayName("[API][Service] 전체 모임 조회 성공 테스트")
        void findAllMeetingSuccess() {
            when(meetingRepository.findByEventDateGreaterThanOrderByEventDateAsc()).thenReturn(meetings);
            List<MeetingListResponseDto> meetingResponses = meetingService.findAllMeetings(requestDto);
            Assertions.assertThat(meetingResponses.size()).isEqualTo(2);
            Assertions.assertThat(meetingResponses.get(0).getId()).isEqualTo(1L);
            Assertions.assertThat(meetingResponses.get(1).getId()).isEqualTo(2L);
        }

    }

    @Nested
    @DisplayName("[API][Service] 유저별 조회 테스트 테스트 - 예정, 참여, 대기")
    class MeetingByUser {

        List<Meeting> meetings = new ArrayList<>();
        User user;
        Meeting meeting;

        @BeforeEach
        void setUp() {
            user = User.builder()
                    .id(1L)
                    .name("홍길동")
                    .profileImg("https://api.scoop.com/user/profile/1.png")
                    .build();

            MeetingType meetingType = new MeetingType(1L, "Play");
            ExerciseLevel exerciseLevel = new ExerciseLevel(1L, "Running", "Beginner");

            GeometryFactory gf = new GeometryFactory();
            Point point = gf.createPoint(new Coordinate(1.3, 1.4));


            MeetingLocation meetingLocation = MeetingLocation.builder()
                    .location(point)
                    .locationName("Konkuk university")
                    .locationDetail("library")
                    .city("Seoul")
                    .build();

            UserMeeting userMeeting = UserMeeting.builder()
                    .user(user)
                    .meeting(meeting)
                    .status(MeetingStatus.OWNER)
                    .build();


            meeting = Meeting.builder()
                    .id(1L)
                    .title("모임 제목")
                    .content("모임 내용")
                    .user(user)
                    .memberCount(1)
                    .viewCount(0)
                    .memberLimit(10)
                    .eventDate(LocalDateTime.now())
                    .exerciseLevel(exerciseLevel)
                    .meetingLocation(meetingLocation)
                    .userMeetings(Collections.singletonList(userMeeting))
                    .meetingType(meetingType)
                    .gender(Gender.MALE)
                    .build();

            Meeting meeting1 = Meeting.builder()
                    .id(2L)
                    .title("모임 제목")
                    .content("모임 내용")
                    .user(user)
                    .memberCount(1)
                    .viewCount(0)
                    .memberLimit(10)
                    .eventDate(LocalDateTime.now())
                    .exerciseLevel(exerciseLevel)
                    .meetingLocation(meetingLocation)
                    .userMeetings(Collections.singletonList(userMeeting))
                    .meetingType(meetingType)
                    .gender(Gender.MALE)
                    .build();

            meetings.add(meeting);
            meetings.add(meeting1);

        }

        @Nested
        @DisplayName("[API][Service] 유저별 예정된 모임 조회 테스트")
        class UpcomingMeeting {

            @Nested
            @DisplayName("[API][Service] 유저별 예정된 모임 조회 성공 테스트")
            class Success {

                @Test
                @DisplayName("[API][Service] 유저별 예정된 모임 조회 성공 테스트")
                void getUpcomingMeetingSuccess() {
                    when(meetingRepository.findUserNextMeeting(any(), any(), any())).thenReturn(meetings);
                    Pageable pageable = PageRequest.of(1, 10);

                    List<MeetingListResponseDto> meetingResponses = meetingService.findUserUpcomingMeeting(user, pageable);
                    Assertions.assertThat(meetingResponses.size()).isEqualTo(2);
                    Assertions.assertThat(meetingResponses.get(0).getId()).isEqualTo(1L);
                    Assertions.assertThat(meetingResponses.get(1).getId()).isEqualTo(2L);
                }

            }

        }

        @Nested
        @DisplayName("[API][Service] 유저별 참여했던 모임 조회 테스트")
        class PastMeeting {

            @Nested
            @DisplayName("[API][Service] 유저별 참여했던 모임 조회 성공 테스트")
            class Success {

                @Test
                @DisplayName("[API][Service] 유저별 참여했던 모임 조회 성공 테스트")
                void getPastMeetingSuccess() {
                    when(meetingRepository.findUserPastMeeting(any(), any(), any())).thenReturn(meetings);
                    Pageable pageable = PageRequest.of(1, 10);

                    List<MeetingListResponseDto> meetingResponses = meetingService.findUserPastMeeting(user, pageable);
                    Assertions.assertThat(meetingResponses.size()).isEqualTo(2);
                    Assertions.assertThat(meetingResponses.get(0).getId()).isEqualTo(1L);
                    Assertions.assertThat(meetingResponses.get(1).getId()).isEqualTo(2L);
                }

            }


        }

        @Nested
        @DisplayName("[API][Service] 유저별 승인 대기중인 모임 조회 테스트")
        class WaitingMeeting {

            @Nested
            @DisplayName("[API][Service] 유저별 승인 대기중인 모임 조회 성공 테스트")
            class Success {

                @Test
                @DisplayName("[API][Service] 유저별 승인 대기중인 모임 조회 성공 테스트")
                void getWaitingMeetingSuccess() {
                    when(meetingRepository.findUserWaitingMeeting(any(), any(), any())).thenReturn(meetings);
                    Pageable pageable = PageRequest.of(1, 10);

                    List<MeetingListResponseDto> meetingResponses = meetingService.findUserWaitingMeeting(user, pageable);
                    Assertions.assertThat(meetingResponses.size()).isEqualTo(2);
                    Assertions.assertThat(meetingResponses.get(0).getId()).isEqualTo(1L);
                    Assertions.assertThat(meetingResponses.get(1).getId()).isEqualTo(2L);
                }

            }

        }


        @Nested
        @DisplayName("[API][Service] 유저별 관리중인 모임 조회 테스트")
        class OwnerMeeting {

            @Nested
            @DisplayName("[API][Service] 유저별 관리중인 모임 조회 성공 테스트")
            class Success {

                @Test
                @DisplayName("[API][Service] 유저별 관리중인 모임 조회 성공 테스트")
                void getOwnerMeetingSuccess() {
                    when(meetingRepository.findUserOwnedMeeting(any(), any())).thenReturn(meetings);
                    Pageable pageable = PageRequest.of(1, 10);

                    List<MeetingListResponseDto> meetingResponses = meetingService.findOwnerMeetingByUser(user, pageable);
                    Assertions.assertThat(meetingResponses.size()).isEqualTo(2);
                    Assertions.assertThat(meetingResponses.get(0).getId()).isEqualTo(1L);
                    Assertions.assertThat(meetingResponses.get(1).getId()).isEqualTo(2L);
                }

            }

        }

        @Nested
        @DisplayName("[API][Service] 유저별 좋아요 표시한 모임 조회 테스트")
        class FindLikeMeeting {

            @Nested
            @DisplayName("[API][Service] 유저별 좋아요 표시한 모임 조회 성공 테스트")
            class Success {

                @Test
                @DisplayName("[API][Service] 유저별 좋아요 표시한 모임 조회 성공 테스트")
                void findLikeMeetingSuccess() {
                    when(meetingRepository.findUserLikedMeeting(any(), any())).thenReturn(meetings);
                    Pageable pageable = PageRequest.of(1, 10);

                    List<MeetingListResponseDto> meetingResponses = meetingService.findLikeMeetingByUser(user, pageable);
                    Assertions.assertThat(meetingResponses.size()).isEqualTo(2);
                    Assertions.assertThat(meetingResponses.get(0).getId()).isEqualTo(1L);
                    Assertions.assertThat(meetingResponses.get(1).getId()).isEqualTo(2L);
                }
            }
        }
    }


    @Nested
    @DisplayName("[API][Service] 모임 좋아요 테스트")
    class LikeMeeting {
        User user;
        Meeting meeting;

        @BeforeEach
        void setUp() {
            user = User.builder()
                    .id(1L)
                    .build();

            meeting = Meeting.builder()
                    .id(1L)
                    .build();
        }


        @Nested
        @DisplayName("[API][Service] 모임 좋아요 성공 테스트")
        class Success {

            @Test
            @DisplayName("[API][Service] 모임 좋아요 성공 테스트")
            void likeMeetingSuccess() {
                when(meetingRepository.findById(any())).thenReturn(Optional.of(meeting));
                when(meetingLikeRepository.save(any())).thenReturn(new MeetingLike());

                meetingService.likeMeeting(user, 1L);

                verify(meetingLikeRepository, times(1)).save(any());
            }


        }

        @Nested
        @DisplayName("[API][Service] 모임 좋아요 실패 테스트")
        class Fail {

            @Test
            @DisplayName("[API][Service] 좋아요할 모임의 id가 존재하지 않을 경우 실패 테스트")
            void likeMeetingFail() {
                when(meetingRepository.findById(any())).thenReturn(Optional.empty());

                Exception e = assertThrows(NotFoundException.class, () -> meetingService.likeMeeting(user, 1L));
                Assertions.assertThat(e.getMessage()).isEqualTo("Not Found the Meeting with id 1");
            }

        }

    }

    @Nested
    @DisplayName("[API][Service] 모임 좋아요 취소 테스트")
    class UnLikeMeeting {
        User user;
        Meeting meeting;
        MeetingLike meetingLike;

        @BeforeEach
        void setUp() {
            user = User.builder()
                    .id(1L)
                    .build();

            meeting = Meeting.builder()
                    .id(1L)
                    .build();

            meetingLike = MeetingLike.builder()
                    .id(1L)
                    .meeting(meeting)
                    .user(user)
                    .build();

        }

        @Nested
        @DisplayName("[API][Service] 모임 좋아요 취소 성공 테스트")
        class Success {

            @Test
            @DisplayName("[API][Service] 모임 좋아요 취소 성공 테스트")
            void unLikeMeetingSuccess() {
                when(meetingRepository.findById(any())).thenReturn(Optional.of(meeting));
                when(meetingLikeRepository.findByMeetingAndUser(any(), any())).thenReturn(Optional.of(meetingLike));

                meetingService.unLikeMeeting(user, 1L);

                verify(meetingLikeRepository, times(1)).delete(any());
            }

        }

        @Nested
        @DisplayName("[API][Service] 모임 좋아요 취소 실패 테스트")
        class Fail {

            @Test
            @DisplayName("[API][Service] 좋아요 취소할 모임의 id가 존재하지 않을 경우 실패 테스트")
            void unLikeMeetingFail() {
                when(meetingRepository.findById(any())).thenReturn(Optional.empty());

                Exception e = assertThrows(NotFoundException.class, () -> meetingService.unLikeMeeting(user, 1L));
                Assertions.assertThat(e.getMessage()).isEqualTo("Not Found the Meeting with id 1");
            }

            @Test
            @DisplayName("[API][Service] 모임을 좋아요 하지 않았을 경우 실패 테스트")
            void unLikeMeetingFail2() {
                when(meetingRepository.findById(any())).thenReturn(Optional.of(meeting));
                when(meetingLikeRepository.findByMeetingAndUser(any(), any())).thenReturn(Optional.empty());

                Exception e = assertThrows(BusinessException.class, () -> meetingService.unLikeMeeting(user, 1L));
                Assertions.assertThat(e.getMessage()).isEqualTo("Not Liked the Meeting");
            }


        }

    }


    @Nested
    @DisplayName("[API][Service] 모임 참여 승인 테스트")
    class AcceptMeeting {
        User owner;
        User participant;

        User others;
        Meeting meeting;

        UserMeeting userMeeting;

        @BeforeEach
        void setUp() {
            owner = User.builder()
                    .id(1L)
                    .build();

            participant = User.builder()
                    .id(2L)
                    .build();

            others = User.builder()
                    .id(3L)
                    .build();

            meeting = Meeting.builder()
                    .id(1L)
                    .user(owner)
                    .memberCount(1)
                    .build();

            userMeeting = UserMeeting.builder()
                    .id(1L)
                    .meeting(meeting)
                    .user(participant)
                    .status(MeetingStatus.WAITING)
                    .build();
        }


        @Nested
        @DisplayName("[API][Service] 모임 참여 승인 성공 테스트")
        class Success {

            @Test
            @DisplayName("[API][Service] Waiting 상태인 유저 모임 참여 승인 성공 테스트")
            void acceptMeetingSuccess() {
                when(userRepository.findById(any())).thenReturn(Optional.of(participant));
                when(meetingRepository.findById(any())).thenReturn(Optional.of(meeting));
                when(userMeetingRepository.findByMeetingAndUser(any(), any())).thenReturn(Optional.of(userMeeting));
                when(userMeetingRepository.save(any())).thenReturn(userMeeting);


                meetingService.acceptMeeting(owner, 1L, 2L);

                Assertions.assertThat(userMeeting.getStatus()).isEqualTo(MeetingStatus.ACCEPTED);
                Assertions.assertThat(meeting.getMemberCount()).isEqualTo(2);
                verify(userMeetingRepository, times(1)).save(any());
            }

            @Test
            @DisplayName("[API][Service] Rejected 상태인 유저 모임 참여 승인 성공 테스트")
            void acceptMeetingSuccess2() {
                userMeeting.setStatus(MeetingStatus.REJECTED);

                when(userRepository.findById(any())).thenReturn(Optional.of(participant));
                when(meetingRepository.findById(any())).thenReturn(Optional.of(meeting));
                when(userMeetingRepository.findByMeetingAndUser(any(), any())).thenReturn(Optional.of(userMeeting));
                when(userMeetingRepository.save(any())).thenReturn(userMeeting);

                meetingService.acceptMeeting(owner, 1L, 2L);
                Assertions.assertThat(userMeeting.getStatus()).isEqualTo(MeetingStatus.ACCEPTED);
                Assertions.assertThat(meeting.getMemberCount()).isEqualTo(2);
                verify(userMeetingRepository, times(1)).save(any());
            }

        }

        @Nested
        @DisplayName("[API][Service] 모임 참여 승인 실패 테스트")
        class Fail {

            @Test
            @DisplayName("[API][Service] 요청한 유저의 id가 존재하지 않을 경우 실패 테스트")
            void acceptMeetingFail() {
                when(userRepository.findById(any())).thenReturn(Optional.empty());

                Exception e = assertThrows(NotFoundException.class, () -> meetingService.acceptMeeting(owner, 1L, 2L));
                Assertions.assertThat(e.getMessage()).isEqualTo("Not Found the User with id 2");
            }

            @Test
            @DisplayName("[API][Service] 모임의 id가 존재하지 않을 경우 실패 테스트")
            void acceptMeetingFail2() {
                when(userRepository.findById(any())).thenReturn(Optional.of(participant));
                when(meetingRepository.findById(any())).thenReturn(Optional.empty());

                Exception e = assertThrows(NotFoundException.class, () -> meetingService.acceptMeeting(owner, 1L, 2L));
                Assertions.assertThat(e.getMessage()).isEqualTo("Not Found the Meeting with id 1");
                Assertions.assertThat(userMeeting.getStatus()).isEqualTo(MeetingStatus.WAITING);
            }

            @Test
            @DisplayName("[API][Service] 모임의 주최자가 아닌 유저에게 요청할 경우 실패 테스트")
            void acceptMeetingFail3() {
                when(userRepository.findById(any())).thenReturn(Optional.of(participant));
                when(meetingRepository.findById(any())).thenReturn(Optional.of(meeting));

                Exception e = assertThrows(BusinessException.class, () -> meetingService.acceptMeeting(others, 1L, 2L));
                Assertions.assertThat(e.getMessage()).isEqualTo("Not Owner of the Meeting");
                Assertions.assertThat(meeting.getMemberCount()).isEqualTo(1);
                Assertions.assertThat(userMeeting.getStatus()).isEqualTo(MeetingStatus.WAITING);
            }

            @Test
            @DisplayName("[API][Service] 요청유저가 모임에 참여대기중 상태가 아닐경우 실패 테스트")
            void acceptMeetingFail4() {
                when(userRepository.findById(any())).thenReturn(Optional.of(participant));
                when(meetingRepository.findById(any())).thenReturn(Optional.of(meeting));
                when(userMeetingRepository.findByMeetingAndUser(any(), any())).thenReturn(Optional.empty());

                Exception e = assertThrows(BusinessException.class, () -> meetingService.acceptMeeting(owner, 1L, 2L));
                Assertions.assertThat(e.getMessage()).isEqualTo("Not Joined User in the Meeting with id 2");
                Assertions.assertThat(meeting.getMemberCount()).isEqualTo(1);
            }

            @Test
            @DisplayName("[API][Service] 요청유저가 이미 모임에 참여중인 상태일 경우 실패 테스트")
            void acceptMeetingFail5() {
                userMeeting.setStatus(MeetingStatus.ACCEPTED);

                when(userRepository.findById(any())).thenReturn(Optional.of(participant));
                when(meetingRepository.findById(any())).thenReturn(Optional.of(meeting));
                when(userMeetingRepository.findByMeetingAndUser(any(), any())).thenReturn(Optional.of(userMeeting));

                Exception e = assertThrows(BusinessException.class, () -> meetingService.acceptMeeting(owner, 1L, 2L));
                Assertions.assertThat(e.getMessage()).isEqualTo("Already Accepted User in the Meeting");
                Assertions.assertThat(meeting.getMemberCount()).isEqualTo(1);
            }

            @Test
            @DisplayName("[API][Service] 요청유저가 모임의 관리자일 경우 실패 테스트")
            void acceptMeetingFail6() {
                UserMeeting ownerUserMeeting = UserMeeting.builder()
                        .user(owner)
                        .meeting(meeting)
                        .status(MeetingStatus.OWNER)
                        .build();

                when(userRepository.findById(any())).thenReturn(Optional.of(owner));
                when(meetingRepository.findById(any())).thenReturn(Optional.of(meeting));
                when(userMeetingRepository.findByMeetingAndUser(any(), any())).thenReturn(Optional.of(ownerUserMeeting));

                Exception e = assertThrows(BusinessException.class, () -> meetingService.acceptMeeting(owner, 1L, 1L));
                Assertions.assertThat(e.getMessage()).isEqualTo("Already Accepted User in the Meeting");
                Assertions.assertThat(meeting.getMemberCount()).isEqualTo(1);
            }

        }
    }


    @Nested
    @DisplayName("[API][Service] 모임 참여 거절 테스트")
    class RejectMeeting {
        User owner;
        User participant;
        User others;
        Meeting meeting;

        UserMeeting userMeeting;

        @BeforeEach
        void setUp() {
            owner = User.builder()
                    .id(1L)
                    .build();

            participant = User.builder()
                    .id(2L)
                    .build();

            others = User.builder()
                    .id(3L)
                    .build();

            meeting = Meeting.builder()
                    .id(1L)
                    .user(owner)
                    .memberCount(1)
                    .build();

            userMeeting = UserMeeting.builder()
                    .id(1L)
                    .meeting(meeting)
                    .user(participant)
                    .status(MeetingStatus.WAITING)
                    .build();
        }

        @Nested
        @DisplayName("[API][Service] 모임 참여 거절 성공 테스트")
        class Success {
            @Test
            @DisplayName("[API][Service] WAITING 상태의 모임 참여자를 거절하는 테스트")
            void rejectMeetingSuccess() {
                when(userRepository.findById(any())).thenReturn(Optional.of(participant));
                when(meetingRepository.findById(any())).thenReturn(Optional.of(meeting));
                when(userMeetingRepository.findByMeetingAndUser(any(), any())).thenReturn(Optional.of(userMeeting));
                when(userMeetingRepository.save(any())).thenReturn(userMeeting);

                meetingService.rejectMeeting(owner, 1L, 2L);
                Assertions.assertThat(userMeeting.getStatus()).isEqualTo(MeetingStatus.REJECTED);
                Assertions.assertThat(meeting.getMemberCount()).isEqualTo(1);
                verify(userMeetingRepository, times(1)).save(any());
            }

            @Test
            @DisplayName("[API][Service] ACCEPTED 상태의 모임 참여자를 거절하는 테스트")
            void rejectMeetingSuccess2() {
                meeting = Meeting.builder()
                        .id(1L)
                        .user(owner)
                        .memberCount(2)
                        .build();
                userMeeting.setStatus(MeetingStatus.ACCEPTED);

                when(userRepository.findById(any())).thenReturn(Optional.of(participant));
                when(meetingRepository.findById(any())).thenReturn(Optional.of(meeting));
                when(userMeetingRepository.findByMeetingAndUser(any(), any())).thenReturn(Optional.of(userMeeting));
                when(userMeetingRepository.save(any())).thenReturn(userMeeting);

                meetingService.rejectMeeting(owner, 1L, 2L);
                Assertions.assertThat(userMeeting.getStatus()).isEqualTo(MeetingStatus.REJECTED);
                Assertions.assertThat(meeting.getMemberCount()).isEqualTo(1);
                verify(userMeetingRepository, times(1)).save(any());
            }
        }

        @Nested
        @DisplayName("[API][Service] 모임 참여 거절 실패 테스트")
        class Fail {

            @Test
            @DisplayName("[API][Service] 요청한 유저의 id가 존재하지 않을 경우 실패 테스트")
            void rejectMeetingFail() {
                when(userRepository.findById(any())).thenReturn(Optional.empty());

                Exception e = assertThrows(BusinessException.class, () -> meetingService.rejectMeeting(owner, 1L, 2L));
                Assertions.assertThat(e.getMessage()).isEqualTo("Not Found the User with id 2");
                Assertions.assertThat(meeting.getMemberCount()).isEqualTo(1);
            }

            @Test
            @DisplayName("[API][Service] 요청한 모임의 id가 존재하지 않을 경우 실패 테스트")
            void rejectMeetingFail2() {
                when(userRepository.findById(any())).thenReturn(Optional.of(participant));
                when(meetingRepository.findById(any())).thenReturn(Optional.empty());

                Exception e = assertThrows(BusinessException.class, () -> meetingService.rejectMeeting(owner, 1L, 2L));
                Assertions.assertThat(e.getMessage()).isEqualTo("Not Found the Meeting with id 1");
                Assertions.assertThat(meeting.getMemberCount()).isEqualTo(1);
            }

            @Test
            @DisplayName("[API][Service] 모임의 주최자가 아닌 유저에게 요청할 경우 실패 테스트")
            void acceptMeetingFail3() {
                when(userRepository.findById(any())).thenReturn(Optional.of(participant));
                when(meetingRepository.findById(any())).thenReturn(Optional.of(meeting));

                Exception e = assertThrows(BusinessException.class, () -> meetingService.rejectMeeting(others, 1L, 2L));
                Assertions.assertThat(e.getMessage()).isEqualTo("Not Owner of the Meeting");
                Assertions.assertThat(meeting.getMemberCount()).isEqualTo(1);
                Assertions.assertThat(userMeeting.getStatus()).isEqualTo(MeetingStatus.WAITING);
            }

            @Test
            @DisplayName("[API][Service] 요청유저가 모임에 참여대기중 상태가 아닐경우 실패 테스트")
            void acceptMeetingFail4() {
                when(userRepository.findById(any())).thenReturn(Optional.of(participant));
                when(meetingRepository.findById(any())).thenReturn(Optional.of(meeting));
                when(userMeetingRepository.findByMeetingAndUser(any(), any())).thenReturn(Optional.empty());

                Exception e = assertThrows(BusinessException.class, () -> meetingService.rejectMeeting(owner, 1L, 2L));
                Assertions.assertThat(e.getMessage()).isEqualTo("Not Joined User in the Meeting with id 2");
                Assertions.assertThat(meeting.getMemberCount()).isEqualTo(1);
            }

            @Test
            @DisplayName("[API][Service] 요청유저가 관리자일 경우 실패 테스트")
            void acceptMeetingFail5() {
                UserMeeting ownerUserMeeting = UserMeeting.builder()
                        .user(owner)
                        .meeting(meeting)
                        .status(MeetingStatus.OWNER)
                        .build();

                when(userRepository.findById(any())).thenReturn(Optional.of(owner));
                when(meetingRepository.findById(any())).thenReturn(Optional.of(meeting));
                when(userMeetingRepository.findByMeetingAndUser(any(), any())).thenReturn(Optional.of(ownerUserMeeting));

                Exception e = assertThrows(BusinessException.class, () -> meetingService.rejectMeeting(owner, 1L, 1L));
                Assertions.assertThat(e.getMessage()).isEqualTo("Owner Cannot Reject Self the Meeting");
                Assertions.assertThat(meeting.getMemberCount()).isEqualTo(1);


            }
        }
    }

    @Nested
    @DisplayName("[API][Service] 모임 이미지 업로드 테스트")
    class uploadMeetingImage {

        MultipartFile multipartFile;

        @Test
        @DisplayName("[API][Service] 모임 이미지 업로드 성공 테스트")
        void uploadMeetingImageSuccess() throws IOException {
            multipartFile = new MockMultipartFile("file", "image.jpg", "image/jpeg", "image".getBytes());
            when(s3Service.upload(any(), any())).thenReturn("image.jpg");

            meetingService.uploadMeetingImage(multipartFile);
            verify(s3Service, times(1)).upload(any(), any());
        }

        @Test
        @DisplayName("[API][Service] 모임 이미지 업로드 실패 테스트")
        void uploadMeetingImageFail() throws IOException {
            multipartFile = new MockMultipartFile("file", "image.jpg", "image/jpeg", "image".getBytes());
            when(s3Service.upload(any(), any())).thenThrow(IOException.class);

            Exception e = assertThrows(BusinessException.class, () -> meetingService.uploadMeetingImage(multipartFile));
            Assertions.assertThat(e.getMessage()).isEqualTo("File Upload Error");
        }

        @Test
        @DisplayName("[API][Service] 모임 이미지 업로드 실패 테스트- 파일이 없는 경우")
        void uploadMeetingImageFail2() {
            multipartFile = new MockMultipartFile("file", "image.jpg", "image/jpeg", "".getBytes());

            Exception e = assertThrows(BusinessException.class, () -> meetingService.uploadMeetingImage(multipartFile));
            Assertions.assertThat(e.getMessage()).isEqualTo("Not Found Upload File");
        }

    }

    @Nested
    @DisplayName("[API][Service] 모임 검색 테스트")
    class searchMeeting {

        Meeting meeting;

        @BeforeEach
        void setUp() {
            User user = User.builder()
                    .id(1L)
                    .name("홍길동")
                    .profileImg("https://api.scoop.com/user/profile/1.png")
                    .build();

            MeetingType meetingType = new MeetingType(1L, "Play");
            ExerciseLevel exerciseLevel = new ExerciseLevel(1L, "Running", "Beginner");

            GeometryFactory gf = new GeometryFactory();
            Point point = gf.createPoint(new Coordinate(1.3, 1.4));


            MeetingLocation meetingLocation = MeetingLocation.builder()
                    .location(point)
                    .locationName("Konkuk university")
                    .locationDetail("library")
                    .city("Seoul")
                    .build();

            UserMeeting userMeeting = UserMeeting.builder()
                    .user(user)
                    .meeting(meeting)
                    .status(MeetingStatus.OWNER)
                    .build();


            meeting = Meeting.builder()
                    .id(1L)
                    .title("soccer")
                    .content("모임 내용")
                    .user(user)
                    .memberCount(1)
                    .viewCount(0)
                    .memberLimit(10)
                    .eventDate(LocalDateTime.now())
                    .exerciseLevel(exerciseLevel)
                    .meetingLocation(meetingLocation)
                    .userMeetings(Collections.singletonList(userMeeting))
                    .meetingType(meetingType)
                    .gender(Gender.MALE)
                    .build();
        }

        @Test
        @DisplayName("[API][Service] 모임 검색 성공 테스트")
        void searchMeetingSuccess() {
            when(meetingRepository.findByTitleContainingOrContentContainingAndEventDateGreaterThan(any(), any(), any(), any())).thenReturn(Collections.singletonList(meeting));

            Pageable pageable = PageRequest.of(0, 10);
            List<MeetingListResponseDto> meetings = meetingService.searchMeetingByKeyword("soccer", pageable);
            Assertions.assertThat(meetings.size()).isEqualTo(1);
            Assertions.assertThat(meetings.get(0).getTitle()).isEqualTo("soccer");
        }

    }
}


