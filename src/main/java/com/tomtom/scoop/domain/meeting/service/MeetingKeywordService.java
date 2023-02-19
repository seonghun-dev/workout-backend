package com.tomtom.scoop.domain.meeting.service;

import com.tomtom.scoop.domain.meeting.model.dto.response.MeetingKeywordResponseDto;
import com.tomtom.scoop.domain.meeting.model.entity.MeetingType;
import com.tomtom.scoop.domain.meeting.repository.MeetingTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MeetingKeywordService {

    private final MeetingTypeRepository meetingTypeRepository;

    public List<MeetingKeywordResponseDto> findAllMeetingKeywords() {
        List<MeetingType> meetingTypes = meetingTypeRepository.findAll();
        return meetingTypes.stream().map(meetingType ->
                MeetingKeywordResponseDto.builder()
                        .id(meetingType.getId())
                        .keyword(meetingType.getName())
                        .build()
        ).toList();
    }


}
