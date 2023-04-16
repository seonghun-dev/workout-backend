package com.tomtom.scoop.domain.user.model.dto.response;

import com.tomtom.scoop.domain.common.Gender;
import com.tomtom.scoop.domain.user.model.dto.ExerciseLevelDto;
import com.tomtom.scoop.domain.user.model.dto.UserLocationDto;
import com.tomtom.scoop.domain.user.model.entity.User;
import com.tomtom.scoop.domain.user.model.entity.UserExerciseLevel;
import com.tomtom.scoop.domain.user.model.entity.UserKeyword;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {

    @Schema(description = "User Id", example = "1L")
    private Long id;

    @Schema(description = "User Name", example = "kimkonkuk")
    private String name;

    @Schema(description = "User Phone Number", example = "010-1234-5678")
    private String phone;

    @Schema(description = "User Nickname", example = "ele")
    private String nickname;

    @Schema(description = "User Review Rating", example = "3")
    private Float rating;

    @Schema(description = "User Gender", example = "MALE")
    private Gender gender;

    @Schema(description = "User Profile Img", example = "https://scoop.com/image")
    private String profileImg;

    @Schema(description = "user Status Message", example = "i'm busy")
    private String statusMessage;

    private List<ExerciseLevelDto> userExerciseLevels;

    private UserLocationDto userLocation;

    private List<String> userKeywords;

    public static UserResponseDto fromEntity(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .phone(user.getPhone())
                .nickname(user.getNickname())
                .rating(user.getRating())
                .gender(user.getGender())
                .profileImg(user.getProfileImg())
                .statusMessage(user.getStatusMessage())
                .userExerciseLevels(user.getUserExerciseLevels().stream()
                        .map(UserExerciseLevel::getExerciseLevel)
                        .map(ExerciseLevelDto::fromEntity).toList())
                .userLocation(UserLocationDto.fromEntity(user.getUserLocation()))
                .userKeywords(user.getUserKeywords().stream().map(UserKeyword::getKeyword).toList())
                .build();
    }


}
