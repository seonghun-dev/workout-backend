package com.tomtom.scoop.domain.user.model.dto.request;

import com.tomtom.scoop.domain.common.Gender;
import com.tomtom.scoop.domain.user.model.dto.UserLocationDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserJoinDto {

    @Schema(description = "User Name", example = "hong")
    @NotNull
    private String name;

    @Schema(description = "User Phone Number", example = "010-1234-5678")
    @NotNull
    private String phone;

    @Schema(description = "User Nickname", example = "coco")
    @NotNull
    private String nickname;

    @Schema(description = "User Gender - MALE, FEMALE", example = "MALE")
    @NotNull
    private Gender gender;

    @Schema(description = "User Device Token", example = "deviceToken")
    @NotNull
    private String deviceToken;

    @Schema(description = "Exercise Level Ids", example = "1,2")
    @NotNull
    private List<Integer> exerciseLevels;

    @NotNull
    private UserLocationDto userLocation;

    @Schema(description = "User Keyword")
    @NotNull
    private List<String> keywords;

    @Schema(description = "User Profile Image Url")
    private String profileImgUrl;
}
