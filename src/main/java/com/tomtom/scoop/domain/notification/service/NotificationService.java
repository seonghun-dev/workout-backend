package com.tomtom.scoop.domain.notification.service;

import com.tomtom.scoop.domain.notification.model.dto.NotificationActionDto;
import com.tomtom.scoop.domain.notification.model.dto.NotificationListResponseDto;
import com.tomtom.scoop.domain.notification.model.entity.Notification;
import com.tomtom.scoop.domain.notification.repository.NotificationRepository;
import com.tomtom.scoop.domain.user.model.entity.User;
import com.tomtom.scoop.global.exception.ErrorCode;
import com.tomtom.scoop.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public List<NotificationListResponseDto> findAllNotifications(User user) {
        List<Notification> notifications = notificationRepository.findAllByUserAndIsDeletedFalse(user);
        return notifications.stream().map(notification ->
                NotificationListResponseDto.builder()
                        .id(notification.getId())
                        .title(notification.getTitle())
                        .content(notification.getContent())
                        .isRead(notification.getIsRead())
                        .createdAt(notification.getCreatedAt())
                        .action(NotificationActionDto.builder()
                                .page(notification.getNotificationAction().getPage())
                                .contentId(notification.getNotificationAction().getContentId())
                                .build())
                        .build()
        ).toList();
    }

    public void markNotificationsAsRead(User user, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow(() -> new NotFoundException(ErrorCode.NOTIFICATION_NOT_FOUND, notificationId));
        if (notification.getUser() != user)
            throw new IllegalArgumentException(String.format("Notification with id %s does not belong to user with id %s", notificationId, user.getId()));
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    public void deleteNotification(User user, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow(() ->  new NotFoundException(ErrorCode.NOTIFICATION_NOT_FOUND, notificationId));
        if (notification.getUser() != user)
            throw new IllegalArgumentException(String.format("Notification with id %s does not belong to user with id %s", notificationId, user.getId()));
        notification.setIsDeleted(true);
        notificationRepository.save(notification);
    }


}
