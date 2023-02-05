package com.tomtom.scoop.domain.meeting.controller;

import com.tomtom.scoop.domain.meeting.model.dto.MeetingDto;
import com.tomtom.scoop.domain.meeting.model.dto.request.MeetingRequestDto;
import com.tomtom.scoop.domain.meeting.model.dto.response.MeetingDetailResponseDto;
import com.tomtom.scoop.domain.meeting.model.dto.response.MeetingListResponseDto;
import com.tomtom.scoop.domain.meeting.service.MeetingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/v1/meetings")
@RequiredArgsConstructor
public class MeetingController {

    private final MeetingService meetingService;

    @GetMapping
    @ResponseBody
    public List<MeetingListResponseDto> findAllMeetings(Pageable pageable) {
        return meetingService.findAllMeetings(pageable);
    }

    @GetMapping("/{id}")
    @ResponseBody
    public MeetingDetailResponseDto findMeetingById(@PathVariable("id") Long id) {
        return meetingService.findMeetingById(id);
    }

    @PostMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public MeetingDto.response createMeeting(@RequestBody MeetingRequestDto meetingRequestDto) {
        return meetingService.createMeeting(meetingRequestDto);
    }

    @PatchMapping("/{id}")
    @ResponseBody
    public MeetingDto.response updateMeeting(
            @PathVariable("id") Long id,
            @RequestBody MeetingDto.request meetingDto) {
        return meetingService.updateMeeting(id, meetingDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMeeting(@PathVariable("id") Long id) {
        meetingService.deleteMeeting(id);
    }

    @GetMapping("/upcoming")
    @ResponseBody
    public List<MeetingListResponseDto> findUpcomingMeetingByUser(@RequestParam("userId") Long userId, Pageable pageable) {
        return meetingService.findUpcomingMeetingByUser(userId, pageable);
    }

    @GetMapping("/past")
    @ResponseBody
    public List<MeetingListResponseDto> findPastMeetingByUser(@RequestParam("userId") Long userId, Pageable pageable) {
        return meetingService.findPastMeetingByUser(userId, pageable);
    }

    @GetMapping("/like")
    @ResponseBody
    public List<MeetingDto.response> findLikeMeetingByUser(@RequestParam("userId") Long userId) {
        return meetingService.findLikeMeetingByUser(userId);
    }

    @GetMapping("/search")
    @ResponseBody
    public List<MeetingDto.response> searchMeetingByKeyword(@RequestParam("keyword") String keyword, Pageable pageable) {
        return meetingService.searchMeetingByKeyword(keyword, pageable);
    }


}
