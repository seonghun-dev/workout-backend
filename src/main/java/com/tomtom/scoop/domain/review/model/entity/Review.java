package com.tomtom.scoop.domain.review.model.entity;

import com.tomtom.scoop.domain.common.BaseTimeEntity;
import com.tomtom.scoop.domain.meeting.model.entity.Meeting;
import com.tomtom.scoop.domain.user.model.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class Review extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_id")
    private Meeting meeting;

    @ManyToOne(fetch = FetchType.LAZY)
    private User receiver;

    @ManyToOne(fetch = FetchType.LAZY)
    private User reviewer;

    @Column(nullable = false)
    private Integer rating;

    @Setter
    @Column(nullable = false)
    private Boolean isReviewerHidden;

    @Setter
    @Column(nullable = false)
    private Boolean isReceiverHidden;

    @Column(nullable = false)
    private String comment;

}

