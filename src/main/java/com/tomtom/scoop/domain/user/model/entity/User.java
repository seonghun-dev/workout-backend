package com.tomtom.scoop.domain.user.model.entity;

import com.tomtom.scoop.domain.common.Gender;
import com.tomtom.scoop.domain.user.model.dto.request.UserJoinDto;
import com.tomtom.scoop.domain.user.model.dto.request.UserUpdateDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(columnDefinition = "VARCHAR(255)")
    private String oauthId;

    @Column(columnDefinition = "VARCHAR(255)")
    private String name;

    @Column(columnDefinition = "VARCHAR(255)")
    private String phone;

    @Column(columnDefinition = "VARCHAR(255)")
    private String nickname;

    private Float rating;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(columnDefinition = "VARCHAR(255)")
    private String deviceToken;

    @Column(columnDefinition = "TINYINT")
    private boolean isDeleted;

    @Column(columnDefinition = "VARCHAR(255)")
    private String profileImg;

    @OneToMany(mappedBy = "user")
    private List<UserExerciseLevel> userExerciseLevels;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_location_id")
    private UserLocation userLocation;

    @OneToMany(mappedBy = "user")
    private List<UserKeyword> userKeywords;

    @Column
    private String statusMessage;

    private User(String oauthId) {
        this.oauthId = oauthId;
    }

    public static User of(String oauthId) {
        return new User(oauthId);
    }

    public void join(UserJoinDto userJoinDto, UserLocation userLocation, List<UserExerciseLevel> userExerciseLevelList, List<UserKeyword> userKeywordList) {
        this.name = userJoinDto.getName();
        this.nickname = userJoinDto.getNickname();
        this.phone = userJoinDto.getPhone();
        this.gender = userJoinDto.getGender();
        this.deviceToken = userJoinDto.getDeviceToken();
        this.userLocation = userLocation;
        this.profileImg = userJoinDto.getProfileImgUrl();
        this.userExerciseLevels = userExerciseLevelList;
        this.userKeywords = userKeywordList;
    }

    public void update(UserUpdateDto userUpdateDto) {
        this.nickname = userUpdateDto.getNickname();
        this.profileImg = userUpdateDto.getProfileImgUrl();
        this.statusMessage = userUpdateDto.getStatusMessage();
    }

}
