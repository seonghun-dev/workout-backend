package com.tomtom.scoop.domain.user.model.dto;

import com.tomtom.scoop.domain.user.model.entity.User;
import lombok.Getter;

@Getter
public class UserProfile {
    private String oauthId;

    private UserProfile(String oauthId){
        this.oauthId = oauthId;
    }

    public static UserProfile of(String oauthId){
        return new UserProfile(oauthId);
    }

    public User toUser(){
        return User.of(oauthId);
    }
}
