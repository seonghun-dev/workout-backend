package com.tomtom.scoop.domain.meeting.controller;

import com.tomtom.scoop.domain.common.model.ResponseDto;
import com.tomtom.scoop.domain.meeting.model.dto.response.MeetingKeywordResponseDto;
import com.tomtom.scoop.domain.meeting.service.MeetingKeywordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/meeting-keywords")
@Tag(name = "Meeting Keyword Controller V1", description = "MEETING KEYWORD API")
@RequiredArgsConstructor
public class MeetingKeywordController {

    private final MeetingKeywordService meetingKeywordService;

    @GetMapping
    @Operation(summary = "Find All Meeting Keywords")
    public ResponseEntity<List<MeetingKeywordResponseDto>> findAllMeetingKeywords() {
        var response = meetingKeywordService.findAllMeetingKeywords();
        return ResponseDto.ok(response);
    }

}
