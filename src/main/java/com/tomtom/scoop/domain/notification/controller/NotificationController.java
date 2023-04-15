package com.tomtom.scoop.domain.notification.controller;

import com.tomtom.scoop.domain.notification.model.dto.NotificationListResponseDto;
import com.tomtom.scoop.domain.notification.service.NotificationService;
import com.tomtom.scoop.domain.user.model.entity.User;
import com.tomtom.scoop.global.annotation.ReqUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @ResponseBody
    public List<NotificationListResponseDto> findAllNotifications(@ReqUser() User user, Pageable pageable) {
        return notificationService.findAllNotifications(user, pageable);
    }

    @PostMapping("/{notificationId}/read")
    public void markNotificationsAsRead(@ReqUser() User user, @PathVariable("notificationId") Long notificationId) {
        notificationService.markNotificationsAsRead(user, notificationId);
    }

    @DeleteMapping("/{notificationId}")
    public void deleteNotification(@ReqUser() User user, @PathVariable("notificationId") Long notificationId) {
        notificationService.deleteNotification(user, notificationId);
    }


}
