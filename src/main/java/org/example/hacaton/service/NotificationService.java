package org.example.hacaton.service;

import org.example.hacaton.model.user.Notification;

import java.util.List;

public interface NotificationService {

    void save(Notification notification);

    List<Notification> getAllNotifications();
}
