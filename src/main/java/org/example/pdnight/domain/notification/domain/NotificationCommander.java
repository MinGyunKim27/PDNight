package org.example.pdnight.domain.notification.domain;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationCommander {

    void save(Notification notification);

    Optional<Notification> findByIdIsReadFalse(Long id);

}
