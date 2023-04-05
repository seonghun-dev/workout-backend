package com.tomtom.scoop.domain.meeting.service;

import com.tomtom.scoop.domain.common.Gender;
import com.tomtom.scoop.domain.meeting.model.dto.request.MeetingRequestDto;
import com.tomtom.scoop.domain.meeting.model.entity.*;
import com.tomtom.scoop.domain.meeting.repository.*;
import com.tomtom.scoop.domain.user.model.entity.Exercise;
import com.tomtom.scoop.domain.user.model.entity.User;
import com.tomtom.scoop.domain.user.repository.ExerciseRepository;
import com.tomtom.scoop.domain.user.repository.UserRepository;
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
import static org.mockito.Mockito.when;


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

                assertThrows(NotFoundException.class, () -> {
                    meetingService.createMeeting(user, meetingRequestDto);
                });

            }

            @Test
            @DisplayName("[API][Service] DB에 존재하지 않는 운동 타입으로 모임 생성시 실패 테스트")
            void createMeetingFail2() {

                when(meetingTypeRepository.findByName(any())).thenReturn(Optional.of(meetingType));
                when(exerciseRepository.findByName(any())).thenReturn(Optional.empty());

                assertThrows(NotFoundException.class, () -> {
                    meetingService.createMeeting(user, meetingRequestDto);
                });

            }
        }


    }

    @Nested
    @DisplayName("[API][Service] 모임 단건 조회 테스트")
    class FindMeetingOne {

        private LocalDateTime today;
        private User owner;
        private User participantUser1;
        private User participantUser2;
        private User participantUser3;
        private Exercise exercise;
        private MeetingType meetingType;
        private MeetingLocation meetingLocation;
        private UserMeeting userMeeting;

        private Meeting meeting;

        private User createUser(Long id) {
            return User.builder()
                    .id(1L)
                    .name("홍길동")
                    .profileImg("https://api.scoop.com/user/profile/" + id +".png")
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

            owner = createUser(1L);
            participantUser1 = createUser(2L);
            participantUser2 = createUser(3L);
            participantUser3 = createUser(4L);

            today = LocalDateTime.now();

            exercise = new Exercise(1L, "Running");
            meetingType = new MeetingType(1L, "Play");
            meetingLocation = new MeetingLocation(1L, "KonKuk University", "Library", "Seoul", null);

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

            }
        }

    }
}
