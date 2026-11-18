package io.github.nmaksimka.bugtrack.web;

import io.github.nmaksimka.bugtrack.notification.NotificationRepository;
import io.github.nmaksimka.bugtrack.security.CurrentUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class LayoutAdvice {

    private final NotificationRepository notifications;

    public LayoutAdvice(NotificationRepository notifications) {
        this.notifications = notifications;
    }

    @ModelAttribute("unreadNotifications")
    public long unreadNotifications(@AuthenticationPrincipal CurrentUser user) {
        return user == null ? 0 : notifications.countByRecipientIdAndReadFalse(user.id());
    }
}
