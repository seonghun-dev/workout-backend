package com.tomtom.scoop.domain.review.model.entity;

import com.tomtom.scoop.domain.common.BaseTimeEntity;
import com.tomtom.scoop.domain.meeting.model.entity.Meeting;
import com.tomtom.scoop.domain.user.model.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Review extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_id")
    private Meeting meeting;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User receiverUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User reviewerUser;

    @Column(nullable = false)
    private Integer rating;

    @Column(nullable = false)
    private Boolean isReviewerHidden;

    @Column(nullable = false)
    private Boolean isReceiverHidden;

    @Column(nullable = false)
    private String comment;

}

