package com.tomtom.scoop.domain.user.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateDto {

    @Schema(description = "User New nickname")
    private String nickname;

    @Schema(description = "")
    private String statusMessage;

    @Schema(description = "")
    private String profileImgUrl;

    @Schema(description = "User Exercise Levels")
    private List<Integer> exerciseLevels;

    @Schema(description = "User Explain Keyword")
    private List<String> keywords;

}
