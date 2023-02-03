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
        private List<UserExerciseLevel> exerciseLevels;
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
        private List<UserExerciseLevel> userExerciseLevels;
        private List<UserLocation> userLocations;
        private List<UserKeyword> userKeywords;

        public response(CustomUserDetails userDetails) {
            this.oauthId = userDetails.getOauthId();
            this.name = userDetails.getName();
            this.phone = userDetails.getPhone();
            this.nickname = userDetails.getNickname();
            this.rating = userDetails.getRating();
            this.age = userDetails.getAge();
            this.gender = userDetails.getGender();
            this.deviceToken = userDetails.getDeviceToken();
            this.isDeleted = userDetails.isDeleted();
            this.profileImg = userDetails.getProfileImg();
        }
    }


}
