package com.tomtom.scoop.domain.notification.model.entity;

import com.tomtom.scoop.domain.common.BaseTimeEntity;
import com.tomtom.scoop.domain.user.model.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Notification extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, columnDefinition = "VARCHAR(255)")
    private String title;

    @Column(nullable = false, columnDefinition = "VARCHAR(255)")
    private String content;

    @Setter
    @Column(nullable = false)
    private Boolean isRead;

    @Setter
    @Column(nullable = false)
    private Boolean isDeleted;

    @OneToOne()
    @JoinColumn(name = "notification_action_id")
    private NotificationAction notificationAction;

}
