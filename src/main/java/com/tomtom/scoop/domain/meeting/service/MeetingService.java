package com.tomtom.scoop.domain.meeting.service;

import com.tomtom.scoop.domain.meeting.model.dto.MeetingDto;
import com.tomtom.scoop.domain.meeting.model.dto.request.MeetingRequestDto;
import com.tomtom.scoop.domain.meeting.model.entity.*;
import com.tomtom.scoop.domain.meeting.repository.MeetingLocationRepository;
import com.tomtom.scoop.domain.meeting.repository.MeetingRepository;
import com.tomtom.scoop.domain.meeting.repository.UserMeetingRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MeetingService {
    private final MeetingRepository meetingRepository;
    private final UserMeetingRepository userMeetingRepository;
    private final MeetingLocationRepository meetingLocationRepository;

    public MeetingDto.response createMeeting(MeetingRequestDto meetingDto) {

        LocalDateTime today = LocalDateTime.now();
        GeometryFactory gf = new GeometryFactory();
        Point point = gf.createPoint(new Coordinate(meetingDto.getLocationLatitude(), meetingDto.getLocationLongitude()));

        MeetingLocation meetingLocation = MeetingLocation.builder()
                .locationName(meetingDto.getLocationName())
                .locationDetail(meetingDto.getLocationDetail())
                .city(meetingDto.getLocationCity())
                .location(point)
                .build();


        Meeting meeting = Meeting.builder()
                .title(meetingDto.getTitle())
                .content(meetingDto.getContent())
                .user(null)
                .memberLimit(meetingDto.getMemberLimit())
                .eventDate(today)
                .viewCount(0)
                .meetingLocation(meetingLocation)
                .gender(meetingDto.getGender()).build();


        // add user to user meeting
        UserMeeting userMeeting = UserMeeting.builder()
                .meeting(meeting)
                .user(null)
                .status(MeetingStatus.OWNER)
                .build();

        meetingLocationRepository.save(meetingLocation);
        meetingRepository.save(meeting);
        userMeetingRepository.save(userMeeting);
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

    public List<MeetingDto.response> searchMeetingByKeyword(String keyword) {
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
