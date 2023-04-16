package com.tomtom.scoop.domain.meeting.model.dto.response;

import com.tomtom.scoop.domain.meeting.model.entity.MeetingType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MeetingKeywordResponseDto {
    private Long id;
    private String keyword;

    public static MeetingKeywordResponseDto fromEntity(MeetingType meetingType) {
        return MeetingKeywordResponseDto.builder().id(meetingType.getId()).keyword(meetingType.getName()).build();
    }
}
