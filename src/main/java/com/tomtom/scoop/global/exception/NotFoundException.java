package com.tomtom.scoop.global.exception;

public class NotFoundException extends BusinessException {
    public NotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }

    public NotFoundException(ErrorCode errorCode, long id) {
        super(errorCode, "id " + id + " is not found");
    }

    public NotFoundException(ErrorCode errorCode, String content) {
        super(errorCode, content + " is not found");
    }
}