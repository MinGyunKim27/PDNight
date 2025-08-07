package org.example.pdnight.domain.notification.infra;

import org.example.pdnight.domain.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationCommanderImpl extends JpaRepository<Notification,Long> {
}
