package com.tomtom.scoop.domain.meeting.model.dto.request;

import com.tomtom.scoop.domain.common.Gender;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

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

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime eventDate;

}
