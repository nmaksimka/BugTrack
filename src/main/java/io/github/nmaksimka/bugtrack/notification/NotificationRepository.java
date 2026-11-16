package io.github.nmaksimka.bugtrack.notification;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    long countByRecipientIdAndReadFalse(UUID recipientId);
}
