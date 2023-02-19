package com.tomtom.scoop.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // Auth
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "", "Unauthorized"),
    JWT_ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "TOKEN-0001", "Access token has expired"),
    JWT_REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "TOKEN-0002", "Refresh token has expired"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN-0003", "Invalid token"),


    // Common
    NOT_FOUND(HttpStatus.NOT_FOUND, "", "Not Found the Contents");


    private final HttpStatus status;
    private final String code;
    private final String message;
}
