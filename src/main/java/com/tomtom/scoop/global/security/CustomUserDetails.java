package com.tomtom.scoop.global.security;

import com.tomtom.scoop.domain.common.Gender;
import com.tomtom.scoop.domain.user.model.entity.User;
import com.tomtom.scoop.domain.user.model.entity.UserExerciseLevel;
import com.tomtom.scoop.domain.user.model.entity.UserKeyword;
import com.tomtom.scoop.domain.user.model.entity.UserLocation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {

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

    public CustomUserDetails(User user) {
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
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return nickname;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
