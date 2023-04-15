package com.tomtom.scoop.domain.notification.repository;

import com.tomtom.scoop.domain.notification.model.entity.Notification;
import com.tomtom.scoop.domain.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllByUserAndIsDeletedFalse(User user);
}