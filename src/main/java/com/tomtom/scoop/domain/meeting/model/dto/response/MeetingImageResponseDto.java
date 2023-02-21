package com.tomtom.scoop.domain.meeting.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MeetingImageResponseDto {
    @Schema(description = "Meeting image url", example = "https://scoop.s3.ap-northeast-2.amazonaws.com/42455.png")
    private String imageUrl;
}
