package org.example.hacaton.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateHackathonRequest {

    @NotBlank(message = "Название хакатона")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    @NotBlank(message = "Описание")
    @Size(max = 200)
    private String description;

    private String fullDescription;

    @NotNull(message = "Дата начала")
    private LocalDateTime startDate;

    @NotNull(message = "Дата конца")
    private LocalDateTime endDate;

    @NotNull(message = "Максимальное количество команд")
    private int maxTeams;

    private long managerId;
}