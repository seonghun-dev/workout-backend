package com.tomtom.scoop.domain.notification.service;

import com.tomtom.scoop.domain.notification.model.Notification;
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

    public List<Notification> findAllNotifications(Long userId, Pageable pageable) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id %s not found", userId)));

        return notificationRepository.findByUser(user, pageable);
    }


}
