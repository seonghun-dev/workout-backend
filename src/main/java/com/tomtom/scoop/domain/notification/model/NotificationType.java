package com.tomtom.scoop.domain.notification.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "notification_type")
public class NotificationType {
    @Id
    private Long id;

    @Column(nullable = false, columnDefinition = "VARCHAR(255)")
    private String name;

}