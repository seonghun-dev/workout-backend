package com.tomtom.scoop.domain.notification.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class NotificationAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_action_id")
    private Long id;

    @Column(nullable = false, columnDefinition = "VARCHAR(255)")
    private String page;

    @Column(nullable = false, columnDefinition = "VARCHAR(255)")
    private Long contentId;

}
