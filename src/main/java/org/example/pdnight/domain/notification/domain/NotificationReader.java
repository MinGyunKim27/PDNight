package org.example.pdnight.domain.notification.domain;

public interface NotificationReader{
    Notification findByIdIsReadFalse(Long id);
}
