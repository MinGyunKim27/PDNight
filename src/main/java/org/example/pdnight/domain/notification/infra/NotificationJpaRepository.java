package org.example.pdnight.domain.notification.infra;

import org.example.pdnight.domain.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationJpaRepository extends JpaRepository <Notification, Long> {

    Optional<Notification> findByIdAndIsReadFalse(Long id);

}