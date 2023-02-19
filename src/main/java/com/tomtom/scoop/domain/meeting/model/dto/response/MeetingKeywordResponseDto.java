package com.tomtom.scoop.domain.meeting.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MeetingKeywordResponseDto {
    private Long id;
    private String keyword;
}
