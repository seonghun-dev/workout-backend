package com.tomtom.scoop.domain.notification.repository;

import com.tomtom.scoop.domain.notification.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}