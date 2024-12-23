package org.example.hacaton.mapper;

import org.example.hacaton.dto.response.NotificationResponse;
import org.example.hacaton.model.user.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "text", source = "text")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "memberId", source = "member.id")
    @Mapping(target = "teamId", source = "team.id")
    @Mapping(target = "wereFrom", source = "wereFrom")
    NotificationResponse toNotificationResponse(Notification notification);

    List<NotificationResponse> toNotificationResponseList(List<Notification> notifications);
}
