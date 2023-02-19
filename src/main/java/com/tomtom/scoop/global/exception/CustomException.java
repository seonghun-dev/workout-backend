package com.tomtom.scoop.global.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private final ErrorCode error;

    public CustomException(ErrorCode e) {
        super(e.getMessage());
        this.error = e;
    }

}
