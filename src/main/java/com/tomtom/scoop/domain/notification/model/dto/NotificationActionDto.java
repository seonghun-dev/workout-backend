package com.tomtom.scoop.domain.notification.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class NotificationActionDto {
    private String page;
    private Long contentId;

}
