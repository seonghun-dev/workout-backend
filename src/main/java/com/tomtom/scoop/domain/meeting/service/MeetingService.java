package com.tomtom.scoop.domain.meeting.service;

import com.tomtom.scoop.domain.meeting.model.dto.MeetingDto;
import com.tomtom.scoop.domain.meeting.model.entity.Meeting;
import com.tomtom.scoop.domain.meeting.repository.MeetingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MeetingService {
    private final MeetingRepository meetingRepository;

    public MeetingDto.response createMeeting(MeetingDto.request meetingDto) {
        Meeting meeting = meetingRepository.save(new Meeting(meetingDto));
        return new MeetingDto.response(
                meeting.getId(),
                meeting.getTitle(),
                meeting.getMemberLimit(),
                meeting.getContent(),
                meeting.getGender(),
                meeting.getImgUrl(),
                meeting.getEventDate(),
                meeting.getCreatedAt()
        );

    }

    public MeetingDto.response findMeetingById(Long id) {
        Meeting meeting = meetingRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 그룹이 없습니다. id=" + id));
        return new MeetingDto.response(
                meeting.getId(),
                meeting.getTitle(),
                meeting.getMemberLimit(),
                meeting.getContent(),
                meeting.getGender(),
                meeting.getImgUrl(),
                meeting.getEventDate(),
                meeting.getCreatedAt()
        );
    }

    public MeetingDto.response deleteMeeting(Long id) {
        Meeting meeting = meetingRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 그룹이 없습니다. id=" + id));
        meetingRepository.delete(meeting);
        return new MeetingDto.response();
    }

    public MeetingDto.response updateMeeting(Long id, MeetingDto.request meetingDto) {
        Meeting meeting = meetingRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 그룹이 없습니다. id=" + id));
        meetingRepository.save(meeting);
        return new MeetingDto.response(
                meeting.getId(),
                meeting.getTitle(),
                meeting.getMemberLimit(),
                meeting.getContent(),
                meeting.getGender(),
                meeting.getImgUrl(),
                meeting.getEventDate(),
                meeting.getCreatedAt()
        );
    }


    public List<MeetingDto.response> findAllMeetings() {
        List<Meeting> meetings = meetingRepository.findAll();
        return meetings.stream().map(meeting -> new MeetingDto.response(
                meeting.getId(),
                meeting.getTitle(),
                meeting.getMemberLimit(),
                meeting.getContent(),
                meeting.getGender(),
                meeting.getImgUrl(),
                meeting.getEventDate(),
                meeting.getCreatedAt()
        )).toList();
    }

    public List<MeetingDto.response> findUpcomingMeetingByUser(Long userId) {
        List<Meeting> meetings = meetingRepository.findAll();
        return meetings.stream().map(meeting -> new MeetingDto.response(
                meeting.getId(),
                meeting.getTitle(),
                meeting.getMemberLimit(),
                meeting.getContent(),
                meeting.getGender(),
                meeting.getImgUrl(),
                meeting.getEventDate(),
                meeting.getCreatedAt()
        )).toList();
    }

    public List<MeetingDto.response> findPastMeetingByUser(Long userId) {
        List<Meeting> meetings = meetingRepository.findAll();
        return meetings.stream().map(meeting -> new MeetingDto.response(
                meeting.getId(),
                meeting.getTitle(),
                meeting.getMemberLimit(),
                meeting.getContent(),
                meeting.getGender(),
                meeting.getImgUrl(),
                meeting.getEventDate(),
                meeting.getCreatedAt()
        )).toList();
    }

    public List<MeetingDto.response> findLikeMeetingByUser(Long userId) {
        List<Meeting> meetings = meetingRepository.findAll();
        return meetings.stream().map(meeting -> new MeetingDto.response(
                meeting.getId(),
                meeting.getTitle(),
                meeting.getMemberLimit(),
                meeting.getContent(),
                meeting.getGender(),
                meeting.getImgUrl(),
                meeting.getEventDate(),
                meeting.getCreatedAt()
        )).toList();
    }

}
