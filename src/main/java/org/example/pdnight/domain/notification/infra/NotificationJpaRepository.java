package org.example.pdnight.domain.notification.infra;

import org.example.pdnight.domain.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface NotificationJpaRepository extends JpaRepository<Notification,Long> {

    @Query("SELECT n FROM Notification n WHERE n.id = :id AND n.isRead = false ")
    Optional<Notification> findByIdIsReadFalse(Long id);
}
