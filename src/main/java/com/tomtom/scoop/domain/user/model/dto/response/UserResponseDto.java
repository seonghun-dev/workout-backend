package com.tomtom.scoop.domain.user.model.dto.response;

import com.tomtom.scoop.domain.common.Gender;
import com.tomtom.scoop.domain.user.model.dto.ExerciseLevelDto;
import com.tomtom.scoop.domain.user.model.dto.UserLocationDto;
import com.tomtom.scoop.domain.user.model.entity.*;
import com.tomtom.scoop.global.security.CustomUserDetails;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
    private Long id;
    private String name;
    private String phone;
    private String nickname;
    private Float rating;
    private Gender gender;
    private String profileImg;
    private String statusMessage;
    private List<ExerciseLevelDto> userExerciseLevels;
    private UserLocationDto userLocation;
    private List<String> userKeywords;


}
