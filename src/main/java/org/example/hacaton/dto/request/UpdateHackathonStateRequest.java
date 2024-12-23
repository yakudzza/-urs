package org.example.hacaton.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateHackathonStateRequest {
    private Long hackathonId;
    private String newState;
}
