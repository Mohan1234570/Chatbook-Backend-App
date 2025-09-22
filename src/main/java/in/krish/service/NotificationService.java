package in.krish.service;

import in.krish.entity.Notification;
import java.util.List;

public interface NotificationService {
    List<Notification> getUnreadNotifications(String userEmail);
    void markAsRead(Long notificationId, String userEmail);
}
