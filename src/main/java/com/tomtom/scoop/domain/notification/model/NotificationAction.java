package com.tomtom.scoop.domain.notification.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class NotificationAction {

    @Id
    @Column(name = "notification_action_id")
    private Long id;

    @Column(nullable = false, columnDefinition = "VARCHAR(255)")
    private String page;

    @Column(nullable = false, columnDefinition = "VARCHAR(255)")
    private Long contentId;

}
