package org.example.hacaton.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.hacaton.model.user.Notification;
import org.example.hacaton.repository.NotificationRepository;
import org.example.hacaton.security.jwt.JwtService;
import org.example.hacaton.service.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    @Transactional
    public void save(Notification notification) {
        notificationRepository.save(notification);
    }

    @Override
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }
}
