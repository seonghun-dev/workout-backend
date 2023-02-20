package com.tomtom.scoop.domain.user.service;

import com.tomtom.scoop.domain.user.model.dto.ExerciseLevelDto;
import com.tomtom.scoop.domain.user.model.dto.UserLocationDto;
import com.tomtom.scoop.domain.user.model.dto.request.UserJoinDto;
import com.tomtom.scoop.domain.user.model.dto.request.UserUpdateDto;
import com.tomtom.scoop.domain.user.model.dto.response.UserResponseDto;
import com.tomtom.scoop.domain.user.model.entity.*;
import com.tomtom.scoop.domain.user.repository.*;
import com.tomtom.scoop.global.exception.BusinessException;
import com.tomtom.scoop.global.exception.ErrorCode;
import com.tomtom.scoop.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final ExerciseLevelRepository exerciseLevelRepository;
    private final ExerciseRepository exerciseRepository;
    private final UserExerciseLevelRepository userExerciseLevelRepository;
    private final UserKeywordRepository userKeywordRepository;
    private final UserLocationRepository userLocationRepository;


    @Transactional(readOnly = true)
    public User findByOauthId(String oauthId) {
        return userRepository.findByOauthId(oauthId).orElseThrow(() -> new BusinessException(ErrorCode.AUTH_ID_NOT_FOUND));
    }

    public UserResponseDto join(User user, UserJoinDto userJoinDto, MultipartFile file) {

        saveUserExerciseLevel(user, userJoinDto.getExerciseLevels());

        saveUserKeyword(userJoinDto.getKeywords(), user);

        UserLocation userLocation = saveUserLocation(userJoinDto.getUserLocation());

        userLocationRepository.save(userLocation);
        user.join(userJoinDto, userLocation, file.getName());
        userRepository.save(user);

        return UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .phone(user.getPhone())
                .nickname(user.getNickname())
                .rating(user.getRating())
                .gender(user.getGender())
                .profileImg(user.getProfileImg())
                .statusMessage(user.getStatusMessage())
                .userExerciseLevels(userJoinDto.getExerciseLevels())
                .userLocation(userJoinDto.getUserLocation())
                .userKeywords(userJoinDto.getKeywords())
                .build();
    }

    public UserResponseDto update(User user, UserUpdateDto userUpdateDto, MultipartFile file) {

        userExerciseLevelRepository.deleteAllByUser(user);

        saveUserExerciseLevel(user, userUpdateDto.getExerciseLevels());

        List<UserKeyword> userKeywords = userKeywordRepository.findByUser(user);
        if (userKeywords != null || !userKeywords.isEmpty()) {
            userKeywordRepository.deleteAllByUser(user);
        }
        saveUserKeyword(userUpdateDto.getKeywords(), user);

        UserLocation userLocation = saveUserLocation(userUpdateDto.getUserLocation());
        userLocationRepository.save(userLocation);
        user.setUserLocation(userLocation);
        user.update(userUpdateDto, file.getName());
        userRepository.save(user);

        return UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .phone(user.getPhone())
                .nickname(user.getNickname())
                .rating(user.getRating())
                .gender(user.getGender())
                .profileImg(user.getProfileImg())
                .statusMessage(user.getStatusMessage())
                .userExerciseLevels(userUpdateDto.getExerciseLevels())
                .userLocation(userUpdateDto.getUserLocation())
                .userKeywords(userUpdateDto.getKeywords())
                .build();
    }

    @Transactional(readOnly = true)
    public UserResponseDto me(User user) {
        user = userRepository.findById(user.getId()).get();
        return UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .phone(user.getPhone())
                .nickname(user.getNickname())
                .rating(user.getRating())
                .gender(user.getGender())
                .profileImg(user.getProfileImg())
                .statusMessage(user.getStatusMessage())
                .userExerciseLevels(user.getUserExerciseLevels().stream().map(
                        userExerciseLevel -> new ExerciseLevelDto(
                                userExerciseLevel.getExerciseLevel().getExercise().getName(),
                                userExerciseLevel.getExerciseLevel().getLevel())
                ).toList())
                .userLocation(user.getUserLocation() == null ? null :
                        new UserLocationDto(
                                user.getUserLocation().getCounty(),
                                user.getUserLocation().getCity(),
                                user.getUserLocation().getDong()))
                .userKeywords(user.getUserKeywords().stream().map(
                        UserKeyword::getKeyword
                ).toList())
                .build();
    }

    public void saveUserExerciseLevel(User user, List<ExerciseLevelDto> exerciseLevels) {
        exerciseLevels.forEach(exerciseLevelDto -> {
            Exercise exercise = exerciseRepository.findByName(exerciseLevelDto.getExerciseName())
                    .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND, exerciseLevelDto.getExerciseName()));
            ExerciseLevel exerciseLevel = exerciseLevelRepository.findByLevelAndExerciseId(exerciseLevelDto.getLevel(), exercise.getId())
                    .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND, exerciseLevelDto.getLevel()));

            UserExerciseLevel userExerciseLevel = UserExerciseLevel.builder().exerciseLevel(exerciseLevel).user(user).build();
            userExerciseLevelRepository.save(userExerciseLevel);
        });
    }

    public void saveUserKeyword(List<String> userKeywords, User user) {
        userKeywords.forEach(keyword -> {
            UserKeyword userKeyword = UserKeyword.builder().keyword(keyword).user(user).build();
            userKeywordRepository.save(userKeyword);
        });
    }

    public UserLocation saveUserLocation(UserLocationDto userLocationDto) {
        Optional<UserLocation> findUserLocation = userLocationRepository.findByCountyAndCityAndDong(
                userLocationDto.getCounty(),
                userLocationDto.getCity(),
                userLocationDto.getDong());
        return findUserLocation.orElseGet(
                () -> UserLocation.builder()
                        .county(userLocationDto.getCounty())
                        .city(userLocationDto.getCity())
                        .dong(userLocationDto.getDong()).build());
    }

}
