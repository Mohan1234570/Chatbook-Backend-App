package in.krish.impl;

import in.krish.entity.Notification;
import in.krish.entity.User;
import in.krish.repo.NotificationRepo;
import in.krish.repo.UserRepo;
import in.krish.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepo notificationRepo;

    @Autowired
    private UserRepo userRepo;

    @Override
    public List<Notification> getUnreadNotifications(String userEmail) {
        User user = userRepo.findByEmailid(userEmail);
        return notificationRepo.findByUserUserIdAndIsReadFalseOrderByCreatedAtDesc(user.getUserId());
    }

    @Override
    public void markAsRead(Long notificationId, String userEmail) {
        User user = userRepo.findByEmailid(userEmail);
        Notification notif = notificationRepo.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (!notif.getUser().equals(user)) {
            throw new RuntimeException("Unauthorized to update this notification");
        }

        notif.setIsRead(true);
        notificationRepo.save(notif);
    }
}

