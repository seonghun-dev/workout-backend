package com.tomtom.scoop.domain.meeting.controller;

import com.tomtom.scoop.domain.meeting.model.dto.MeetingDto;
import com.tomtom.scoop.domain.meeting.service.MeetingService;
import lombok.RequiredArgsConstructor;
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
    public List<MeetingDto.response> findAllMeetings() {
        return meetingService.findAllMeetings();
    }

    @GetMapping("/{id}")
    @ResponseBody
    public MeetingDto.response findMeetingById(@PathVariable("id") Long id) {
        return meetingService.findMeetingById(id);
    }

    @PostMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public MeetingDto.response createMeeting(@RequestBody MeetingDto.request meetingDto) {
        return meetingService.createMeeting(meetingDto);
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
    public List<MeetingDto.response> findUpcomingMeetingByUser(@RequestParam("userId") Long userId) {
        return meetingService.findUpcomingMeetingByUser(userId);
    }

    @GetMapping("/past")
    @ResponseBody
    public List<MeetingDto.response> findPastMeetingByUser(@RequestParam("userId") Long userId) {
        return meetingService.findPastMeetingByUser(userId);
    }

    @GetMapping("/like")
    @ResponseBody
    public List<MeetingDto.response> findLikeMeetingByUser(@RequestParam("userId") Long userId) {
        return meetingService.findLikeMeetingByUser(userId);
    }

    @GetMapping("/search")
    @ResponseBody
    public List<MeetingDto.response> searchMeetingByKeyword(@RequestParam("keyword") String keyword) {
        return meetingService.searchMeetingByKeyword(keyword);
    }



}
