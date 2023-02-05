package com.tomtom.scoop.domain.meeting.model.dto.request;

import com.tomtom.scoop.domain.common.Gender;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MeetingRequestDto {

    @NotNull
    private String title;

    @NotNull
    private String content;

    @NotNull
    private Integer memberLimit;

    @NotNull
    private Gender gender;

    @NotNull
    private Float locationLatitude;

    @NotNull
    private Float locationLongitude;

    private String locationName;

    private String locationDetail;

    private String locationCity;

    private String exerciseName;

    private String exerciseLevel;

    private String meetingType;


}
