package com.tomtom.scoop.domain.meeting.service;

import com.tomtom.scoop.domain.common.Gender;
import com.tomtom.scoop.domain.meeting.model.dto.request.MeetingRequestDto;
import com.tomtom.scoop.domain.meeting.model.entity.*;
import com.tomtom.scoop.domain.meeting.repository.*;
import com.tomtom.scoop.domain.user.model.entity.Exercise;
import com.tomtom.scoop.domain.user.model.entity.User;
import com.tomtom.scoop.domain.user.repository.ExerciseRepository;
import com.tomtom.scoop.domain.user.repository.UserRepository;
import com.tomtom.scoop.global.exception.BusinessException;
import com.tomtom.scoop.global.exception.NotFoundException;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("[API][Service] 모임 관련 테스트")
public class MeetingServiceTest {

    @InjectMocks
    private MeetingService meetingService;

    @Mock
    MeetingRepository meetingRepository;

    @Mock
    UserMeetingRepository userMeetingRepository;

    @Mock
    MeetingLocationRepository meetingLocationRepository;

    @Mock
    MeetingTypeRepository meetingTypeRepository;

    @Mock
    ExerciseRepository exerciseRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    MeetingLikeRepository meetingLikeRepository;


    @Nested
    @DisplayName("[API][Service] 모임 생성 테스트")
    class CreateMeeting {

        private User user;
        private MeetingRequestDto meetingRequestDto;
        private MeetingType meetingType;
        private Meeting meeting;
        private Exercise exercise;

        private MeetingLocation meetingLocation;

        private UserMeeting userMeeting;

        @BeforeEach
        void setUp() {
            //given
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
                    .exerciseName("Running")
                    .exerciseLevel("Beginner")
                    .meetingType("Play")
                    .build();

            meetingType = new MeetingType(1L, "Play");
            exercise = new Exercise(1L, "Running");

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
                    .exerciseLevel(meetingRequestDto.getExerciseLevel())
                    .exercise(exercise)
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
                when(exerciseRepository.findByName(any())).thenReturn(Optional.of(exercise));
                when(meetingLocationRepository.save(any(MeetingLocation.class))).thenReturn(meetingLocation);
                when(meetingRepository.save(any(Meeting.class))).thenReturn(meeting);
                when(userMeetingRepository.save(any(UserMeeting.class))).thenReturn(userMeeting);

                var result = meetingService.createMeeting(user, meetingRequestDto);

                System.out.println("result = " + result);

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
                when(exerciseRepository.findByName(any())).thenReturn(Optional.empty());

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

            Exercise exercise = new Exercise(1L, "Running");
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
                    .exerciseLevel("Beginner")
                    .exercise(exercise)
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

        @Nested
        @DisplayName("[API][Service] 모임 참여 성공 테스트")
        class Success {

        }

        @Nested
        @DisplayName("[API][Service] 모임 참여 실패 테스트")
        class Fail {

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

}
