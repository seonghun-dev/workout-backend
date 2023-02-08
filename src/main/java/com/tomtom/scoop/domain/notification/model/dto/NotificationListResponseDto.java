package com.tomtom.scoop.domain.notification.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class NotificationListResponseDto {

    private Long id;

    private String title;

    private String content;

    private Boolean isRead;

    private LocalDateTime createdAt;

    private NotificationActionDto action;

}

