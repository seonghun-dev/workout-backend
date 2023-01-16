package com.tomtom.scoop.domain.notification.model;

import com.tomtom.scoop.domain.common.BaseTimeEntity;
import com.tomtom.scoop.domain.user.model.entity.User;
import jakarta.persistence.*;

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

    @Column(nullable = false)
    private Boolean isRead;

    @Column(nullable = false)
    private Boolean isDeleted;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "notification_action_id")
    private NotificationAction notificationAction;

    @ManyToOne
    @JoinColumn(name = "notification_type_id")
    private NotificationType notificationType;

}
