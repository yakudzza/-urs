package org.example.hacaton.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamStatusResponse {

    private long id;
    private String name;
    private int membersCount;
    private String status;
}