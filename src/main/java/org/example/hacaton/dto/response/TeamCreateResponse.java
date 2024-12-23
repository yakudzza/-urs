package org.example.hacaton.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamCreateResponse {

    private String name;
    private String hackathonTitle;
    private String managerName;
    private String creatorName;
    private Set<String> members;
}
