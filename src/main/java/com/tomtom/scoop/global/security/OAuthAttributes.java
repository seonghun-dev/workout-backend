package com.tomtom.scoop.global.security;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

public enum OAuthAttributes {

    KAKAO("kakao", (attributes) -> UserProfile.of(String.valueOf(attributes.get("id"))));


    private final String registrationId;
    private final Function<Map<String, Object>, UserProfile> of;

    OAuthAttributes(String registrationId, Function<Map<String, Object>, UserProfile> of) {
        this.registrationId = registrationId;
        this.of = of;
    }

    public static UserProfile extract(String registrationId, Map<String, Object> attributes) throws IllegalAccessException {
        return Arrays.stream(values())
                .filter(provider -> registrationId.equals(provider.registrationId))
                .findFirst()
                .orElseThrow(IllegalAccessException::new)
                .of.apply(attributes);
    }
}
