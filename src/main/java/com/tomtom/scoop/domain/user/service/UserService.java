package com.tomtom.scoop.domain.user.service;

import com.tomtom.scoop.domain.exercise.model.entity.ExerciseLevel;
import com.tomtom.scoop.domain.exercise.repository.ExerciseLevelRepository;
import com.tomtom.scoop.domain.user.model.dto.ExerciseLevelDto;
import com.tomtom.scoop.domain.user.model.dto.UserLocationDto;
import com.tomtom.scoop.domain.user.model.dto.request.UserJoinDto;
import com.tomtom.scoop.domain.user.model.dto.request.UserUpdateDto;
import com.tomtom.scoop.domain.user.model.dto.response.UserResponseDto;
import com.tomtom.scoop.domain.user.model.entity.User;
import com.tomtom.scoop.domain.user.model.entity.UserExerciseLevel;
import com.tomtom.scoop.domain.user.model.entity.UserKeyword;
import com.tomtom.scoop.domain.user.model.entity.UserLocation;
import com.tomtom.scoop.domain.user.repository.UserExerciseLevelRepository;
import com.tomtom.scoop.domain.user.repository.UserKeywordRepository;
import com.tomtom.scoop.domain.user.repository.UserLocationRepository;
import com.tomtom.scoop.domain.user.repository.UserRepository;
import com.tomtom.scoop.global.exception.BusinessException;
import com.tomtom.scoop.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserExerciseLevelRepository userExerciseLevelRepository;
    private final ExerciseLevelRepository exerciseLevelRepository;
    private final UserKeywordRepository userKeywordRepository;
    private final UserLocationRepository userLocationRepository;


    @Transactional(readOnly = true)
    public User findByOauthId(String oauthId) {
        return userRepository.findByOauthId(oauthId).orElseThrow(() -> new BusinessException(ErrorCode.AUTH_ID_NOT_FOUND));
    }

    @Transactional
    public UserResponseDto join(User user, UserJoinDto userJoinDto) {
        List<ExerciseLevel> exerciseLevelList = exerciseLevelRepository.findAllById(userJoinDto.getExerciseLevels().stream().map(Integer::longValue).toList());
        List<UserExerciseLevel> userExerciseLevelList = exerciseLevelList.stream().map(v -> UserExerciseLevel.builder().exerciseLevel(v).user(user).build()).toList();
        List<UserKeyword> userKeywordList = userJoinDto.getKeywords().stream().map(keyword -> UserKeyword.builder().keyword(keyword).user(user).build()).toList();

        userExerciseLevelRepository.saveAll(userExerciseLevelList);
        userKeywordRepository.saveAll(userKeywordList);

        UserLocationDto userLocationDto = userJoinDto.getUserLocation();
        GeometryFactory gf = new GeometryFactory();
        Point point = gf.createPoint(new Coordinate(userLocationDto.getLatitude(), userLocationDto.getLongitude()));
        UserLocation userLocation = UserLocation.builder().location(point).locationRange(100)
                .isVerified(true).verifiedDate(LocalDateTime.now())
                .county(userLocationDto.getCounty())
                .city(userLocationDto.getCity())
                .build();

        userLocationRepository.save(userLocation);

        user.join(userJoinDto, userLocation);
        userRepository.save(user);

        List<ExerciseLevelDto> exerciseLevelDtoList = exerciseLevelList.stream().map(v -> new ExerciseLevelDto(v.getExercise(), v.getLevel())).toList();

        return UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .phone(user.getPhone())
                .nickname(user.getNickname())
                .rating(user.getRating())
                .gender(user.getGender())
                .profileImg(user.getProfileImg())
                .statusMessage(user.getStatusMessage())
                .userExerciseLevels(exerciseLevelDtoList)
                .userLocation(userJoinDto.getUserLocation())
                .userKeywords(userJoinDto.getKeywords())
                .build();
    }

    public UserResponseDto update(User user, UserUpdateDto userUpdateDto) {
        List<ExerciseLevel> exerciseLevelList = exerciseLevelRepository.findAllById(userUpdateDto.getExerciseLevels().stream().map(Integer::longValue).toList());

        List<UserExerciseLevel> userExerciseLevels = userExerciseLevelRepository.findAllByUser(user);
        List<UserExerciseLevel> userExerciseLevelsToDelete = userExerciseLevels.stream()
                .filter(userExerciseLevel -> exerciseLevelList.stream().noneMatch(exerciseLevel -> Objects.equals(exerciseLevel.getId(), userExerciseLevel.getId())))
                .collect(Collectors.toList());

        List<UserExerciseLevel> userExerciseLevelsToSave = exerciseLevelList.stream()
                .filter(exerciseLevel -> userExerciseLevels.stream().noneMatch(userExerciseLevel -> Objects.equals(userExerciseLevel.getId(), exerciseLevel.getId())))
                .map(exerciseLevel -> UserExerciseLevel.builder().exerciseLevel(exerciseLevel).user(user).build())
                .collect(Collectors.toList());

        userExerciseLevelRepository.deleteAll(userExerciseLevelsToDelete);
        userExerciseLevelRepository.saveAll(userExerciseLevelsToSave);

        List<UserKeyword> userKeywordList = userKeywordRepository.findByUser(user);
        if (userKeywordList != null) {
            userKeywordRepository.deleteAllByUser(user);
        }
        var userKeywords = userUpdateDto.getKeywords();
        userKeywords.stream().map(e -> UserKeyword.builder().keyword(e).user(user).build()).forEach(userKeywordRepository::save);

        user.update(userUpdateDto);
        userRepository.save(user);

        var userLocationDto = UserLocationDto.builder().county(user.getUserLocation().getCounty()).city(user.getUserLocation().getCity())
                .latitude((float) user.getUserLocation().getLocation().getCoordinate().getX())
                .longitude((float) user.getUserLocation().getLocation().getCoordinate().getY()).build();


        return UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .phone(user.getPhone())
                .nickname(user.getNickname())
                .rating(user.getRating())
                .gender(user.getGender())
                .profileImg(user.getProfileImg())
                .statusMessage(user.getStatusMessage())
                .userLocation(userLocationDto)
                .userKeywords(userUpdateDto.getKeywords())
                .build();
    }

    @Transactional(readOnly = true)
    public UserResponseDto me(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .phone(user.getPhone())
                .nickname(user.getNickname())
                .rating(user.getRating())
                .gender(user.getGender())
                .profileImg(user.getProfileImg())
                .statusMessage(user.getStatusMessage())
                .userKeywords(user.getUserKeywords().stream().map(
                        UserKeyword::getKeyword
                ).toList())
                .build();
    }

    public UserResponseDto updateUserLocation(User user, UserLocationDto userLocationDto) {
        return UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .phone(user.getPhone())
                .nickname(user.getNickname())
                .rating(user.getRating())
                .gender(user.getGender())
                .profileImg(user.getProfileImg())
                .statusMessage(user.getStatusMessage())
                .userKeywords(user.getUserKeywords().stream().map(
                        UserKeyword::getKeyword
                ).toList())
                .build();
    }


}
