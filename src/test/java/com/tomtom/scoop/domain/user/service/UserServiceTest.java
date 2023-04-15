package com.tomtom.scoop.domain.user.service;


import com.tomtom.scoop.domain.common.Gender;
import com.tomtom.scoop.domain.exercise.model.entity.ExerciseLevel;
import com.tomtom.scoop.domain.exercise.repository.ExerciseLevelRepository;
import com.tomtom.scoop.domain.user.model.dto.ExerciseLevelDto;
import com.tomtom.scoop.domain.user.model.dto.UserLocationDto;
import com.tomtom.scoop.domain.user.model.dto.request.UserJoinDto;
import com.tomtom.scoop.domain.user.model.dto.response.UserResponseDto;
import com.tomtom.scoop.domain.user.model.entity.User;
import com.tomtom.scoop.domain.user.model.entity.UserExerciseLevel;
import com.tomtom.scoop.domain.user.model.entity.UserKeyword;
import com.tomtom.scoop.domain.user.model.entity.UserLocation;
import com.tomtom.scoop.domain.user.repository.UserExerciseLevelRepository;
import com.tomtom.scoop.domain.user.repository.UserKeywordRepository;
import com.tomtom.scoop.domain.user.repository.UserLocationRepository;
import com.tomtom.scoop.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("[API][Service] 유저 관련 테스트")
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserKeywordRepository userKeywordRepository;

    @Mock
    private ExerciseLevelRepository exerciseLevelRepository;

    @Mock
    private UserLocationRepository userLocationRepository;

    @Mock
    private UserExerciseLevelRepository userExerciseLevelRepository;

    @InjectMocks
    private UserService userService;


    @Nested
    @DisplayName("[API][Service] 유저 가입 테스트")
    class JoinUser {

        @Nested
        @DisplayName("[API][Service] 유저 가입 성공 테스트")
        class Success {

            @Test
            @DisplayName("[API][Service] 유저 가입 성공 테스트")
            void JoinUser() {
                User user = User.builder().id(1L).build();
                UserLocationDto userLocationDto = UserLocationDto.builder().county("Korea").city("Seoul").latitude(37.5419F).longitude(127.0738F).build();
                UserJoinDto userJoinDto = UserJoinDto.builder().name("hong")
                        .phone("010-1234-5678").nickname("coco")
                        .gender(Gender.MALE).deviceToken("deviceToken")
                        .exerciseLevels(List.of(1, 2)).userLocation(userLocationDto)
                        .keywords(List.of("ISTP", "Poor")).profileImgUrl("https://scoop/image").build();

                List<ExerciseLevel> exerciseLevelList = List.of(
                        new ExerciseLevel(1L, "Running", "Beginner"),
                        new ExerciseLevel(2L, "Soccer", "Beginner")
                );

                List<UserKeyword> userKeywordList = List.of(
                        new UserKeyword(1L, "ISTP", user),
                        new UserKeyword(2L, "Poor", user)
                );

                List<UserExerciseLevel> userExerciseLevelList = List.of(
                        UserExerciseLevel.builder().exerciseLevel(new ExerciseLevel(1L, "Running", "Beginner")).user(user).build(),
                        UserExerciseLevel.builder().exerciseLevel(new ExerciseLevel(2L, "Soccer", "Beginner")).user(user).build()
                );

                UserLocation userLocation = UserLocation.builder().id(1L).county("korea").city("Seoul").build();
                UserResponseDto expectedResponse = UserResponseDto.builder()
                        .id(1L).name("hong").phone("010-1234-5678").nickname("coco").gender(Gender.MALE)
                        .profileImg("https://scoop/image").userLocation(userJoinDto.getUserLocation())
                        .userKeywords(List.of("ISTP", "Poor"))
                        .userExerciseLevels(List.of(new ExerciseLevelDto("Running", "Beginner"), new ExerciseLevelDto("Soccer", "Beginner")))
                        .build();

                when(exerciseLevelRepository.findAllById(List.of(1L, 2L))).thenReturn(exerciseLevelList);
                when(userExerciseLevelRepository.saveAll(any())).thenReturn(userExerciseLevelList);
                when(userKeywordRepository.saveAll(any())).thenReturn(userKeywordList);
                when(userLocationRepository.save(any())).thenReturn(userLocation);
                when(userRepository.save(any())).thenReturn(user);

                UserResponseDto result = userService.join(user, userJoinDto);

                assertAll(
                        () -> assertThat(result).usingRecursiveComparison().isEqualTo(expectedResponse),
                        () -> verify(exerciseLevelRepository).findAllById(List.of(1L, 2L)),
                        () -> verify(userExerciseLevelRepository).saveAll(any()),
                        () -> verify(userKeywordRepository).saveAll(any()),
                        () -> verify(userLocationRepository).save(any()),
                        () -> verify(userRepository).save(any())
                );
            }
        }

        @Nested
        @DisplayName("[API][Service] 유저 가입 실패 테스트")
        class Fail {


        }


    }


}
