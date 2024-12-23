package org.example.hacaton.service;

import org.example.hacaton.dto.request.CreateHackathonRequest;
import org.example.hacaton.dto.response.HackathonListResponse;
import org.example.hacaton.dto.response.HackathonResponse;
import org.example.hacaton.model.hackathon.Hackathon;
import org.example.hacaton.model.user.Manager;

import java.util.List;

public interface HackathonService {

    HackathonResponse createHackathon(CreateHackathonRequest createHackathonRequest, String token);

    List<HackathonListResponse> getAllHackathon();

    Manager getManagerInHackathon(Long id);

    HackathonResponse getHackathonById(Long id);

    Hackathon addManagerToHackathon(Long hackathonId, Long managerId);

    void updateHackathonState(Long hackathonId, String newState);

    void createHackathonSticker(String name, String description);
}
