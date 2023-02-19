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
    NOT_FOUND(HttpStatus.NOT_FOUND, "", "Not Found the Contents"),



    // Meeting
    MEETING_NOT_FOUND(HttpStatus.NOT_FOUND, "MEETING-0001", "Not Found the Meeting"),
    MEETING_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND, "MEETING-0002", "Not Found the Meeting Type"),
    ALREADY_JOINED_MEETING(HttpStatus.BAD_REQUEST, "MEETING-0003", "Already Joined the Meeting"),
    NOT_MEETING_OWNER(HttpStatus.FORBIDDEN, "MEETING-0004", "Not Owner of the Meeting"),

    // Exercise
    EXERCISE_NOT_FOUND(HttpStatus.NOT_FOUND, "EXERCISE-0001", "Not Found the Exercise"),
    EXERCISE_LEVEL_NOT_FOUND(HttpStatus.NOT_FOUND, "EXERCISE-0002", "Not Found the Exercise Level");


    private final HttpStatus status;
    private final String code;
    private final String message;
}
