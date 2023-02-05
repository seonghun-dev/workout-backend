package com.tomtom.scoop.domain.meeting.model.entity;

import com.tomtom.scoop.domain.common.BaseTimeEntity;
import com.tomtom.scoop.domain.common.Gender;
import com.tomtom.scoop.domain.user.model.entity.ExerciseLevel;
import com.tomtom.scoop.domain.user.model.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Meeting extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meeting_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
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
    private Integer viewCount;

    @Column(nullable = false)
    @Setter
    private boolean isDeleted;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_location_id")
    private MeetingLocation meetingLocation;

    @OneToMany(mappedBy = "meeting")
    private List<MeetingLike> meetingLikes;

    @OneToMany(mappedBy = "meeting")
    private List<UserMeeting> userMeetings;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_level_id")
    private ExerciseLevel exerciseLevel;

}
