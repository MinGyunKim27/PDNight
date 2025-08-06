package org.example.pdnight.domain.notification.domain;

import java.util.Optional;

public interface NotificationCommander {
    
    Notification save(Notification notification);

    Optional<Notification> findByIdIsReadFalse(Long id);
    
}