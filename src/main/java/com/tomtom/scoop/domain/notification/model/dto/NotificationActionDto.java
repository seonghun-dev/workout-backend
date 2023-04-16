package com.tomtom.scoop.domain.notification.model.dto;

import com.tomtom.scoop.domain.notification.model.entity.NotificationAction;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class NotificationActionDto {

    @Schema(description = "The page that the notification links to", example = "Meeting")
    private String page;

    @Schema(description = "The ID of the content related to the notification", example = "123")
    private Long contentId;

    public static NotificationActionDto fromEntity(NotificationAction notificationAction) {
        return NotificationActionDto.builder()
                .page(notificationAction.getPage())
                .contentId(notificationAction.getContentId())
                .build();
    }

}
