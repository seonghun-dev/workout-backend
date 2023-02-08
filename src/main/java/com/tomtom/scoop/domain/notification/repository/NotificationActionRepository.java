package com.tomtom.scoop.domain.notification.repository;

import com.tomtom.scoop.domain.notification.model.NotificationAction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationActionRepository extends JpaRepository<NotificationAction, Long> {
}