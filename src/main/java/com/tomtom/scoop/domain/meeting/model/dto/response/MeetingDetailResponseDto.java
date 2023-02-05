package com.tomtom.scoop.domain.meeting.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class MeetingDetailResponseDto {

    private Long id;

    private String title;

    private Integer memberCount;

    private Integer memberLimit;

    private String content;

    private String meetingType;

    private String exerciseLevel;

    private String ownerName;

    private String ownerProfile;

    private List<String> meetingUserProfiles;

    private String status;

    private Boolean isLiked;

}
