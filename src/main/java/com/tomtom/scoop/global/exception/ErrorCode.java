package com.tomtom.scoop.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // Common
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "C-0000", "Bad Request"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "C-0001", "Not Found the Contents"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C-0002", "Internal Server Error"),
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "C-0003", "Not Found Upload File"),
    FILE_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C-0004", "File Upload Error"),

    // Auth
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "AUTH-0001", "Unauthorized"),
    AUTH_ID_NOT_FOUND(HttpStatus.NOT_FOUND, "AUTH-0002", "Not Found the Auth Id"),
    JWT_ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "TOKEN-0001", "Access token has expired"),
    JWT_REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "TOKEN-0002", "Refresh token has expired"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN-0003", "Invalid token"),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER-0001", "Not Found the User"),

    // Meeting
    MEETING_NOT_FOUND(HttpStatus.NOT_FOUND, "MEETING-0001", "Not Found the Meeting"),
    MEETING_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND, "MEETING-0002", "Not Found the Meeting Type"),
    ALREADY_JOINED_MEETING(HttpStatus.BAD_REQUEST, "MEETING-0003", "Already Joined the Meeting"),
    NOT_MEETING_OWNER(HttpStatus.FORBIDDEN, "MEETING-0004", "Not Owner of the Meeting"),
    NOT_LIKED_MEETING(HttpStatus.BAD_REQUEST, "MEETING-0005", "Not Liked the Meeting"),
    NOT_JOINED_USER_IN_MEETING(HttpStatus.BAD_REQUEST, "MEETING-0006", "Not Joined User in the Meeting"),
    OWNER_CANNOT_LEAVE_MEETING(HttpStatus.BAD_REQUEST, "MEETING-0007", "Owner Cannot Leave the Meeting"),
    REJECTED_USER_CANNOT_LEAVE_MEETING(HttpStatus.BAD_REQUEST, "MEETING-0008", "Rejected User Cannot Leave the Meeting"),
    ALREADY_ACCEPTED_USER_IN_MEETING(HttpStatus.BAD_REQUEST, "MEETING-0009", "Already Accepted User in the Meeting"),
    OWNER_CANNOT_REJECT_MEETING(HttpStatus.BAD_REQUEST, "MEETING-0010", "Owner Cannot Reject Self the Meeting"),

    // Exercise
    EXERCISE_NOT_FOUND(HttpStatus.NOT_FOUND, "EXERCISE-0001", "Not Found the Exercise"),
    EXERCISE_LEVEL_NOT_FOUND(HttpStatus.NOT_FOUND, "EXERCISE-0002", "Not Found the Exercise Level"),


    // Review
    REVIEW_RECEIVER_USER_NOT_FOUNT(HttpStatus.NOT_FOUND, "REVIEW-0001", "Not Found Review Receiver User"),
    REVIEW_DELETE_AUTHORITY_NOT_MATCH(HttpStatus.BAD_REQUEST, "REVIEW-0002", "Writer Or Who received the review Can Delete Review"),
    REVIEW_RECEIVER_NOT_PARTICIPATE_MEETING(HttpStatus.BAD_REQUEST,"REVIEW-0003", "Who received the review did not participate meeting"),
    REVIEW_WRITER_NOT_PARTICIPATE_MEETING(HttpStatus.BAD_REQUEST,"REVIEW-0004", "Writer did not participate meeting"),
    NOT_REVIEW_USER_SELF(HttpStatus.BAD_REQUEST, "REVIEW-0005", "Can't Review Your Self"),
    REVIEW_ALREADY_WRITTEN(HttpStatus.BAD_REQUEST, "REVIEW-0006", "Review Already Written"),
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "REVIEW-0003", "Not Found Review"),


    // Notification
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "NOTIFICATION_0001", "Not Found Notification");


    private final HttpStatus status;
    private final String code;
    private final String message;
}
