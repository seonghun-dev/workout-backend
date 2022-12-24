package com.tomtom.scoop.domain.notice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
public class NoticeDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class request {
        private String title;
        private String content;
    }


    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class response {
        private Long id;
        private String title;
        private String content;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

}
