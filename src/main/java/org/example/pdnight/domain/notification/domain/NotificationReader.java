package org.example.pdnight.domain.notification.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationReader {

    Notification findById(Long id);

    Page<Notification> findAll(Long userId, Pageable pageable);

    Page<Notification> findIsReadFalse(Long userId, Pageable pageable);
}
