package com.tomtom.scoop.domain.meeting.controller;

import com.tomtom.scoop.domain.meeting.model.dto.request.MeetingRequestDto;
import com.tomtom.scoop.domain.meeting.model.dto.response.MeetingDetailResponseDto;
import com.tomtom.scoop.domain.meeting.model.dto.response.MeetingListResponseDto;
import com.tomtom.scoop.domain.meeting.service.MeetingService;
import com.tomtom.scoop.domain.user.model.entity.User;
import com.tomtom.scoop.domain.user.repository.UserRepository;
import com.tomtom.scoop.domain.common.model.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;


@RestController
@RequestMapping("/v1/meetings")
@Tag(name = "Meeting Controller V1", description = "MEETING API")
@RequiredArgsConstructor
public class MeetingController {

    private final MeetingService meetingService;
    private final UserRepository userRepository;

    @PostMapping
    @Operation(summary = "Create a meeting")
    @Transactional
    public ResponseEntity<MeetingDetailResponseDto> createMeeting(
            Principal principal,
            @RequestBody MeetingRequestDto meetingRequestDto) {
        var response = meetingService.createMeeting(getUser(principal), meetingRequestDto);
        return ResponseDto.created(response);
    }


    @GetMapping
    @Operation(summary = "Find All Meetings")
    public ResponseEntity<List<MeetingListResponseDto>> findAllMeetings(Pageable pageable) {
        var response = meetingService.findAllMeetings(pageable);
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
    public ResponseEntity<MeetingDetailResponseDto> joinMeeting(Principal principal, @PathVariable("meetingId") Long meetingId) {
        var response = meetingService.joinMeeting(getUser(principal), meetingId);
        return ResponseDto.ok(response);
    }

    @PostMapping("/{meetingId}/quit")
    @Operation(summary = "Quit a meeting")
    @Transactional
    public ResponseEntity<MeetingDetailResponseDto> quitMeeting(Principal principal, @PathVariable("meetingId") Long meetingId) {
        var response = meetingService.quitMeeting(getUser(principal), meetingId);
        return ResponseDto.ok(response);
    }

    @PostMapping("/{meetingId}/user/{userId}/accept")
    @Operation(summary = "Accept a meeting request by owner")
    @Transactional
    public ResponseEntity<MeetingDetailResponseDto> acceptMeeting(Principal principal, @PathVariable("meetingId") Long meetingId, @PathVariable("userId") Long requestUserId) {
        var response = meetingService.acceptMeeting(getUser(principal), meetingId, requestUserId);
        return ResponseDto.ok(response);
    }

    @PostMapping("/{meetingId}/user/{userId}/reject")
    @Operation(summary = "Reject a meeting request by owner")
    @Transactional
    public ResponseEntity<MeetingDetailResponseDto> rejectMeeting(Principal principal, @PathVariable("meetingId") Long meetingId, @PathVariable("userId") Long requestUserId) {
        var response = meetingService.rejectMeeting(getUser(principal), meetingId, requestUserId);
        return ResponseDto.ok(response);
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a meeting")
    public ResponseEntity<Void> deleteMeeting(Principal principal, @PathVariable("id") Long id) {
        meetingService.deleteMeeting(getUser(principal), id);
        return ResponseDto.noContent();
    }

    @GetMapping("/upcoming")
    @Operation(summary = "Find upcoming meetings by user")
    public ResponseEntity<List<MeetingListResponseDto>> findUserUpcomingMeeting(Principal principal, Pageable pageable) {
        var response = meetingService.findUserUpcomingMeeting(getUser(principal), pageable);
        return ResponseDto.ok(response);
    }

    @GetMapping("/past")
    @Operation(summary = "Find past meetings by user")
    public ResponseEntity<List<MeetingListResponseDto>> findUserPastMeeting(Principal principal, Pageable pageable) {
        var response = meetingService.findUserPastMeeting(getUser(principal), pageable);
        return ResponseDto.ok(response);
    }

    @GetMapping("/waiting")
    @Operation(summary = "Find waiting meetings by user")
    public ResponseEntity<List<MeetingListResponseDto>> findWaitingMeetingByUser(Principal principal, Pageable pageable) {
        var response = meetingService.findUserWaitingMeeting(getUser(principal), pageable);
        return ResponseDto.ok(response);
    }

    @GetMapping("/owner")
    @Operation(summary = "Find owner meetings by user")
    public ResponseEntity<List<MeetingListResponseDto>> findOwnerMeetingByUser(Principal principal, Pageable pageable) {
        var response = meetingService.findOwnerMeetingByUser(getUser(principal), pageable);
        return ResponseDto.ok(response);
    }

    @GetMapping("/like")
    @Operation(summary = "Find like meetings by user")
    public ResponseEntity<List<MeetingListResponseDto>> findLikeMeetingByUser(Principal principal, Pageable pageable) {
        var response = meetingService.findLikeMeetingByUser(getUser(principal), pageable);
        return ResponseDto.ok(response);
    }

    @PostMapping("/{meetingId}/like")
    @Operation(summary = "Like a meeting")
    public ResponseEntity<MeetingDetailResponseDto> likeMeeting(Principal principal, @PathVariable("meetingId") Long meetingId) {
        var response = meetingService.likeMeeting(getUser(principal), meetingId);
        return ResponseDto.ok(response);
    }

    @PostMapping("/{meetingId}/unlike")
    @Operation(summary = "Unlike a meeting")
    public ResponseEntity<MeetingDetailResponseDto> unlikeMeeting(Principal principal, @PathVariable("meetingId") Long meetingId) {
        var response =  meetingService.unlikeMeeting(getUser(principal), meetingId);
        return ResponseDto.ok(response);
    }

    @GetMapping("/search")
    @Operation(summary = "Search meetings by keyword in title and content")
    public ResponseEntity<List<MeetingListResponseDto>> searchMeetingByKeyword(@RequestParam("keyword") String keyword, Pageable pageable) {
        var response =  meetingService.searchMeetingByKeyword(keyword, pageable);
        return ResponseDto.ok(response);
    }

    private User getUser(Principal principal) {
        String authId = principal.getName();
        return userRepository.findByOauthId(authId).orElseThrow(
                () -> new IllegalArgumentException("User not found")
        );
    }

}
