package com.tomtom.scoop.domain.meeting.model.entity;

import com.tomtom.scoop.domain.common.BaseTimeEntity;
import com.tomtom.scoop.domain.common.Gender;
import com.tomtom.scoop.domain.meeting.model.dto.MeetingDto;
import com.tomtom.scoop.domain.user.model.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Meeting extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meeting_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, columnDefinition = "VARCHAR(255)")
    private String title;

    @ManyToOne
    @JoinColumn(name = "meeting_type_id")
    private MeetingType meetingType;

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
    private Point location;

    @Column(nullable = false)
    private String locationName;

    @Column(nullable = false)
    private Integer viewCount;

    @OneToMany(mappedBy = "meeting")
    private List<MeetingLike> meetingLikes;

    @OneToMany(mappedBy = "meeting")
    private List<UserMeeting> userMeetings;

    public Meeting(MeetingDto.request meetingDto) {
        this.title = meetingDto.getTitle();
        this.memberLimit = meetingDto.getMemberLimit();
        this.content = meetingDto.getContent();
        this.gender = Gender.BOTH;
        this.imgUrl = meetingDto.getImgUrl();
        this.viewCount = 0;
    }

    public void setMeetingType(MeetingType meetingType) {
        this.meetingType = meetingType;
    }

}
