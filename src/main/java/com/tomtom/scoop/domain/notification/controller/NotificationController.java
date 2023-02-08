package com.tomtom.scoop.domain.notification.controller;

import com.tomtom.scoop.domain.notification.model.Notification;
import com.tomtom.scoop.domain.notification.service.NotificationService;
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
    public List<Notification> findAllNotifications(@RequestParam("userId") Long userId,
                                                   Pageable pageable) {
        return notificationService.findAllNotifications(userId, pageable);
    }


}
