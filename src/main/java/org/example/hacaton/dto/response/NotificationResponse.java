package org.example.hacaton.dto.response;

import lombok.Builder;
import lombok.Data;
import org.example.hacaton.model.user.NotificationStatus;
import org.example.hacaton.model.user.WereFrom;

import java.time.LocalDateTime;

@Data
@Builder
public class NotificationResponse {
    private Long id;
    private String text;
    private NotificationStatus status;
    private LocalDateTime createdAt;
    private long memberId;
    private long teamId;
    private WereFrom wereFrom;
}
