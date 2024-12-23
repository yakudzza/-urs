package org.example.hacaton.mapper;

import org.example.hacaton.dto.response.ManagerListResponse;
import org.example.hacaton.model.user.Manager;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ManagerMapper {

    @Mapping(source = "user.firstname", target = "firstname") // Берем имя пользователя из вложенной сущности
    ManagerListResponse toManagerListResponse(Manager manager);
}