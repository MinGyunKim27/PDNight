package org.example.pdnight.domain.notification.infra;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.notification.domain.Notification;
import org.example.pdnight.domain.notification.domain.NotificationCommander;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class NotificationCommanderImpl implements NotificationCommander {

    private final NotificationJpaRepository notificationJpaRepository;

    @Override
    public void save(Notification notification) {
        notificationJpaRepository.save(notification);
    }

    @Override
    public Optional<Notification> findByIdIsReadFalse(Long id) {
        return notificationJpaRepository.findByIdIsReadFalse(id);
    }
}
