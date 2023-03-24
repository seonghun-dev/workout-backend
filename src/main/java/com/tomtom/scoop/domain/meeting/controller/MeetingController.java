package com.tomtom.scoop.domain.meeting.controller;

import com.tomtom.scoop.domain.common.model.ResponseDto;
import com.tomtom.scoop.domain.meeting.model.dto.request.FindAllMeetingRequestDto;
import com.tomtom.scoop.domain.meeting.model.dto.request.MeetingRequestDto;
import com.tomtom.scoop.domain.meeting.model.dto.response.MeetingDetailResponseDto;
import com.tomtom.scoop.domain.meeting.model.dto.response.MeetingImageResponseDto;
import com.tomtom.scoop.domain.meeting.model.dto.response.MeetingListResponseDto;
import com.tomtom.scoop.domain.meeting.service.MeetingService;
import com.tomtom.scoop.domain.user.model.entity.User;
import com.tomtom.scoop.global.annotation.ReqUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("/v1/meetings")
@Tag(name = "Meeting Controller V1", description = "MEETING API")
@RequiredArgsConstructor
public class MeetingController {

    private final MeetingService meetingService;

    @PostMapping
    @Operation(summary = "Create a meeting")
    @Transactional
    public ResponseEntity<MeetingDetailResponseDto> createMeeting(
            @ReqUser() User user,
            @RequestBody MeetingRequestDto meetingRequestDto) {
        var response = meetingService.createMeeting(user, meetingRequestDto);
        return ResponseDto.created(response);
    }

    @PostMapping(value = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Upload meeting image")
    public ResponseEntity<MeetingImageResponseDto> uploadMeetingImage(
            @Parameter(description = "multipart/form-data 형식의 이미지를 input으로 받습니다. key 값은 multipartFile 입니다.")
            @RequestPart("multipartFile") MultipartFile file) {
        var response = meetingService.uploadMeetingImage(file);
        return ResponseDto.created(response);
    }


    @GetMapping
    @Operation(summary = "Find All Meetings")
    public ResponseEntity<List<MeetingListResponseDto>> findAllMeetings(FindAllMeetingRequestDto findAllMeetingRequestDto
    ) {
        var response = meetingService.findAllMeetings(findAllMeetingRequestDto);
        return ResponseDto.ok(response);
    }

    @GetMapping("/{meetingId}")
    @Operation(summary = "Find a meeting by id")
    public ResponseEntity<MeetingDetailResponseDto> findMeetingById(@PathVariable("meetingId") Long meetingId) {
        var response = meetingService.findMeetingById(meetingId);
        return ResponseDto.ok(response);
    }


    @PostMapping("/{meetingId}/join")
    @Operation(summary = "Join a meeting")
    @Transactional
    public ResponseEntity<MeetingDetailResponseDto> joinMeeting(@ReqUser() User user, @PathVariable("meetingId") Long meetingId) {
        var response = meetingService.joinMeeting(user, meetingId);
        return ResponseDto.ok(response);
    }

    @PostMapping("/{meetingId}/quit")
    @Operation(summary = "Quit a meeting")
    @Transactional
    public ResponseEntity<MeetingDetailResponseDto> quitMeeting(@ReqUser() User user, @PathVariable("meetingId") Long meetingId) {
        var response = meetingService.quitMeeting(user, meetingId);
        return ResponseDto.ok(response);
    }

    @PostMapping("/{meetingId}/user/{userId}/accept")
    @Operation(summary = "Accept a meeting request by owner")
    @Transactional
    public ResponseEntity<MeetingDetailResponseDto> acceptMeeting(@ReqUser() User user, @PathVariable("meetingId") Long meetingId, @PathVariable("userId") Long requestUserId) {
        var response = meetingService.acceptMeeting(user, meetingId, requestUserId);
        return ResponseDto.ok(response);
    }

    @PostMapping("/{meetingId}/user/{userId}/reject")
    @Operation(summary = "Reject a meeting request by owner")
    @Transactional
    public ResponseEntity<MeetingDetailResponseDto> rejectMeeting(@ReqUser() User user, @PathVariable("meetingId") Long meetingId, @PathVariable("userId") Long requestUserId) {
        var response = meetingService.rejectMeeting(user, meetingId, requestUserId);
        return ResponseDto.ok(response);
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a meeting")
    public ResponseEntity<Void> deleteMeeting(@ReqUser() User user, @PathVariable("id") Long id) {
        meetingService.deleteMeeting(user, id);
        return ResponseDto.noContent();
    }

    @GetMapping("/upcoming")
    @Operation(summary = "Find upcoming meetings by user")
    public ResponseEntity<List<MeetingListResponseDto>> findUserUpcomingMeeting(@ReqUser() User user, Pageable pageable) {
        var response = meetingService.findUserUpcomingMeeting(user, pageable);
        return ResponseDto.ok(response);
    }

    @GetMapping("/past")
    @Operation(summary = "Find past meetings by user")
    public ResponseEntity<List<MeetingListResponseDto>> findUserPastMeeting(@ReqUser() User user, Pageable pageable) {
        var response = meetingService.findUserPastMeeting(user, pageable);
        return ResponseDto.ok(response);
    }

    @GetMapping("/waiting")
    @Operation(summary = "Find waiting meetings by user")
    public ResponseEntity<List<MeetingListResponseDto>> findWaitingMeetingByUser(@ReqUser() User user, Pageable pageable) {
        var response = meetingService.findUserWaitingMeeting(user, pageable);
        return ResponseDto.ok(response);
    }

    @GetMapping("/owner")
    @Operation(summary = "Find owner meetings by user")
    public ResponseEntity<List<MeetingListResponseDto>> findOwnerMeetingByUser(@ReqUser() User user, Pageable pageable) {
        var response = meetingService.findOwnerMeetingByUser(user, pageable);
        return ResponseDto.ok(response);
    }

    @GetMapping("/like")
    @Operation(summary = "Find like meetings by user")
    public ResponseEntity<List<MeetingListResponseDto>> findLikeMeetingByUser(@ReqUser() User user, Pageable pageable) {
        var response = meetingService.findLikeMeetingByUser(user, pageable);
        return ResponseDto.ok(response);
    }

    @PostMapping("/{meetingId}/like")
    @Operation(summary = "Like a meeting")
    public ResponseEntity<MeetingDetailResponseDto> likeMeeting(@ReqUser() User user, @PathVariable("meetingId") Long meetingId) {
        var response = meetingService.likeMeeting(user, meetingId);
        return ResponseDto.ok(response);
    }

    @PostMapping("/{meetingId}/unlike")
    @Operation(summary = "Unlike a meeting")
    public ResponseEntity<MeetingDetailResponseDto> unlikeMeeting(@ReqUser() User user, @PathVariable("meetingId") Long meetingId) {
        var response = meetingService.unlikeMeeting(user, meetingId);
        return ResponseDto.ok(response);
    }

    @GetMapping("/search")
    @Operation(summary = "Search meetings by keyword in title and content")
    public ResponseEntity<List<MeetingListResponseDto>> searchMeetingByKeyword(@RequestParam("keyword") String keyword, Pageable pageable) {
        var response = meetingService.searchMeetingByKeyword(keyword, pageable);
        return ResponseDto.ok(response);
    }

}
