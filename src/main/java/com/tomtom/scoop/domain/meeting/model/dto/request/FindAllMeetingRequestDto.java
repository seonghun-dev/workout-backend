package com.tomtom.scoop.domain.meeting.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;

@Getter
@AllArgsConstructor
public class FindAllMeetingRequestDto {

    @Schema(description = "Page number", example = "1")
    private Optional<Integer> page;

    @Schema(description = "Page size", example = "10")
    private Optional<Integer> size;

}
