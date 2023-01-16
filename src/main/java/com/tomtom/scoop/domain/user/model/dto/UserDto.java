package com.tomtom.scoop.domain.user.model.dto;

import com.tomtom.scoop.domain.common.Gender;
import com.tomtom.scoop.domain.user.model.entity.*;
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
        private List<UserExerciseLevel> userExerciseLevels;
        private List<UserLocation> userLocations;
        private List<UserKeyword> userKeywords;
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

        public response(User user) {
            this.id = user.getId();
            this.oauthId = user.getOauthId();
            this.name = user.getName();
            this.phone = user.getPhone();
            this.nickname = user.getNickname();
            this.rating = user.getRating();
            this.age = user.getAge();
            this.gender = user.getGender();
            this.deviceToken = user.getDeviceToken();
            this.isDeleted = user.isDeleted();
            this.profileImg = user.getProfileImg();
            this.userExerciseLevels = user.getUserExerciseLevels();
            this.userKeywords = user.getUserKeywords();
        }
    }


}
