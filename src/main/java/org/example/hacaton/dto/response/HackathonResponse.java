package org.example.hacaton.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.hacaton.model.hackathon.State;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HackathonResponse {

    private String title;
    private String fullDescription;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private State state;
    private String managerName;
    private int teamSize;
    private int maxTeams;
}
