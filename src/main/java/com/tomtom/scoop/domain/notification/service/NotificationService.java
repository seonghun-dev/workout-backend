package com.tomtom.scoop.domain.notification.service;

import com.tomtom.scoop.domain.notification.model.dto.NotificationResponseDto;
import com.tomtom.scoop.domain.notification.model.entity.Notification;
import com.tomtom.scoop.domain.notification.repository.NotificationRepository;
import com.tomtom.scoop.domain.user.model.entity.User;
import com.tomtom.scoop.global.exception.BusinessException;
import com.tomtom.scoop.global.exception.ErrorCode;
import com.tomtom.scoop.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public List<NotificationResponseDto> findAllNotifications(User user) {
        return notificationRepository.findAllByUserAndIsDeletedFalse(user).stream()
                .map(NotificationResponseDto::fromEntity).toList();
    }

    public void markNotificationsAsRead(User user, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow(() -> new NotFoundException(ErrorCode.NOTIFICATION_NOT_FOUND, notificationId));
        if (notification.getUser() != user)
            throw new BusinessException(ErrorCode.NOT_USER_NOTIFICATION);
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    public void deleteNotification(User user, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow(() -> new NotFoundException(ErrorCode.NOTIFICATION_NOT_FOUND, notificationId));
        if (notification.getUser() != user)
            throw new BusinessException(ErrorCode.NOT_USER_NOTIFICATION);
        notification.setIsDeleted(true);
        notificationRepository.save(notification);
    }

}
