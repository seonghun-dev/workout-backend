package com.tomtom.scoop.domain.user.model.dto;

import com.tomtom.scoop.domain.common.Gender;
import com.tomtom.scoop.domain.user.model.entity.*;
import com.tomtom.scoop.global.security.CustomUserDetails;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
public class UserDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class request {
        private String name;
        private String phone;
        private String nickname;
        private Float rating;
        private Integer age;
        private Gender gender;
        private String profileImg;
        private String StatusMessage;
        private List<ExerciseLevelDto> exerciseLevels;
        private List<UserLocation> locations;
        private List<String> keywords;
    }


    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class response {
        private Long id;
        private String oauthId;
        private String name;
        private String phone;
        private String nickname;
        private Float rating;
        private Integer age;
        private Gender gender;
        private String deviceToken;
        private boolean isDeleted;
        private String profileImg;
        private String statusMessage;
        private List<UserExerciseLevel> userExerciseLevels;
        private List<UserLocation> userLocations;
        private List<UserKeyword> userKeywords;

        public response(CustomUserDetails userDetails) {
            this.oauthId = userDetails.getOauthId();
            this.name = userDetails.getUser().getName();
            this.phone = userDetails.getUser().getPhone();
            this.nickname = userDetails.getUser().getNickname();
            this.rating = userDetails.getUser().getRating();
            this.age = userDetails.getUser().getAge();
            this.gender = userDetails.getUser().getGender();
            this.deviceToken = userDetails.getUser().getDeviceToken();
            this.isDeleted = userDetails.getUser().isDeleted();
            this.profileImg = userDetails.getUser().getProfileImg();
            this.statusMessage = userDetails.getUser().getStatusMessage();
        }
    }


}
