package org.example.hacaton.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamInfoResponse {
    private String teamName;
    private String hackathonName;
}