package com.tomtom.scoop.domain.meeting.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class MeetingListResponseDto {

    private Long id;

    private String title;

    private String city;

    private LocalDateTime eventDate;

    private Integer memberCount;

    private Integer memberLimit;

    private String imgUrl;

    private String exerciseName;

    private String exerciseLevel;

    private String meetingType;

}
