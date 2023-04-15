package com.tomtom.scoop.domain.notification.model.dto;

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

}
