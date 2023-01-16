package com.tomtom.scoop.domain.meeting.model.dto;

import com.tomtom.scoop.domain.common.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
public class MeetingDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class request {
        private String title;
        private Integer memberLimit;
        private String content;
        private String gender;
        private String imgUrl;
        private String eventDate;
    }


    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class response {
        private Long id;
        private String title;
        private Integer memberLimit;
        private String content;
        private Gender gender;
        private String imgUrl;
        private LocalDateTime eventDate;
        private LocalDateTime createdAt;


    }


}
