package com.tomtom.scoop.domain.notification.model.dto;

import com.tomtom.scoop.domain.notification.model.entity.Notification;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class NotificationResponseDto {

    @Schema(description = "The unique identifier of the notification.", example = "1")
    private Long id;

    @Schema(description = "The title of the notification.", example = "New message received")
    private String title;

    @Schema(description = "The content of the notification.", example = "You have received a new message from John.")
    private String content;

    @Schema(description = "Whether the notification has been marked as read.", example = "false")
    private Boolean isRead;

    @Schema(description = "The date and time when the notification was created.", example = "2022-05-01T12:30:45")
    private LocalDateTime createdAt;

    private NotificationActionDto action;

    public static NotificationResponseDto fromEntity(Notification notification){
        return NotificationResponseDto.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .content(notification.getContent())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .action(NotificationActionDto.fromEntity(notification.getNotificationAction()))
                .build();
    }

}

