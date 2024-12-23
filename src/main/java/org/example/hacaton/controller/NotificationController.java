package org.example.hacaton.controller;

import lombok.RequiredArgsConstructor;
import org.example.hacaton.dto.response.NotificationResponse;
import org.example.hacaton.mapper.NotificationMapper;
import org.example.hacaton.model.user.Notification;
import org.example.hacaton.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;


@RestController
@RequestMapping("api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationMapper notificationMapper;

    @GetMapping("/all")
    public ResponseEntity<List<NotificationResponse>> getAllNotifications() {
        try {
            // Получаем список всех уведомлений
            List<Notification> notifications = notificationService.getAllNotifications();

            // Преобразуем их в DTO с помощью маппера
            List<NotificationResponse> response = notificationMapper.toNotificationResponseList(notifications);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }
}