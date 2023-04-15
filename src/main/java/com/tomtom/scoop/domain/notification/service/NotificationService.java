package com.tomtom.scoop.domain.notification.service;

import com.tomtom.scoop.domain.notification.model.dto.NotificationActionDto;
import com.tomtom.scoop.domain.notification.model.dto.NotificationListResponseDto;
import com.tomtom.scoop.domain.notification.model.entity.Notification;
import com.tomtom.scoop.domain.notification.repository.NotificationRepository;
import com.tomtom.scoop.domain.user.model.entity.User;
import com.tomtom.scoop.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    private final UserRepository userRepository;

    public List<NotificationListResponseDto> findAllNotifications(User user, Pageable pageable) {
        List<Notification> notifications = notificationRepository.findByUser(user, pageable);
        return notifications.stream().map(
                notification ->
                        NotificationListResponseDto.builder()
                                .id(notification.getId())
                                .title(notification.getTitle())
                                .content(notification.getContent())
                                .isRead(notification.getIsRead())
                                .createdAt(notification.getCreatedAt())
                                .action(NotificationActionDto.builder()
                                        .page(notification.getNotificationAction().getPage())
                                        .contentId(notification.getNotificationAction().getContentId())
                                        .build()).build()
        ).toList();
    }

    public void markNotificationsAsRead(User user, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotFoundException(String.format("Notification with id %s not found", notificationId)));
        if (notification.getUser() != user)
            throw new IllegalArgumentException(String.format("Notification with id %s does not belong to user with id %s", notificationId, user.getId()));
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    public void deleteNotification(User user, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotFoundException(String.format("Notification with id %s not found", notificationId)));
        if (notification.getUser() != user)
            throw new IllegalArgumentException(String.format("Notification with id %s does not belong to user with id %s", notificationId, user.getId()));
        notification.setIsDeleted(true);
        notificationRepository.save(notification);
    }


}
