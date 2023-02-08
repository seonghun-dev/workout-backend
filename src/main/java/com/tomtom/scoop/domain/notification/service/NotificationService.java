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
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    private final UserRepository userRepository;

    public List<NotificationListResponseDto> findAllNotifications(Long userId, Pageable pageable) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id %s not found", userId)));
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

    public void markNotificationsAsRead(Long userId, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotFoundException(String.format("Notification with id %s not found", notificationId)));
        if (notification.getUser() != null && !Objects.equals(notification.getUser().getId(), userId))
            throw new IllegalArgumentException(String.format("Notification with id %s does not belong to user with id %s", notificationId, userId));
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    public void deleteNotification(Long userId, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotFoundException(String.format("Notification with id %s not found", notificationId)));
        if (notification.getUser() != null && !Objects.equals(notification.getUser().getId(), userId))
            throw new IllegalArgumentException(String.format("Notification with id %s does not belong to user with id %s", notificationId, userId));
        notification.setIsDeleted(true);
        notificationRepository.save(notification);
    }


}
