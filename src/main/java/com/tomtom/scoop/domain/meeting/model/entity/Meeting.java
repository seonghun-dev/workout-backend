package com.tomtom.scoop.domain.meeting.model.entity;

import com.tomtom.scoop.domain.common.BaseTimeEntity;
import com.tomtom.scoop.domain.meeting.model.dto.MeetingDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Meeting extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "VARCHAR(255)")
    private String title;

    @Column(nullable = false)
    private Integer memberLimit;

    @Column(nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(columnDefinition = "VARCHAR(255)")
    private String imgUrl;

    @Column(nullable = false)
    private LocalDateTime eventDate;

    @Column(nullable = false)
    private Integer viewCount;

    public Meeting(MeetingDto.request meetingDto) {
        this.title = meetingDto.getTitle();
        this.memberLimit = meetingDto.getMemberLimit();
        this.content = meetingDto.getContent();
        this.gender = Gender.BOTH;
        this.imgUrl = meetingDto.getImgUrl();
        this.viewCount = 0;
    }

}
