package com.tomtom.scoop.domain.notification.controller;

import com.tomtom.scoop.domain.common.model.ResponseDto;
import com.tomtom.scoop.domain.notification.model.dto.NotificationListResponseDto;
import com.tomtom.scoop.domain.notification.service.NotificationService;
import com.tomtom.scoop.domain.user.model.entity.User;
import com.tomtom.scoop.global.annotation.ReqUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @ResponseBody
    public ResponseEntity<List<NotificationListResponseDto>> findAllNotifications(@ReqUser() User user) {
        var response = notificationService.findAllNotifications(user);
        return ResponseDto.ok(response);
    }

    @PostMapping("/{notificationId}/read")
    public ResponseEntity<Void> markNotificationsAsRead(@ReqUser() User user, @PathVariable("notificationId") Long notificationId) {
        notificationService.markNotificationsAsRead(user, notificationId);
        return ResponseDto.noContent();
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void>  deleteNotification(@ReqUser() User user, @PathVariable("notificationId") Long notificationId) {
        notificationService.deleteNotification(user, notificationId);
        return ResponseDto.noContent();
    }

}
