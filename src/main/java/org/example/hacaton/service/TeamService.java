package org.example.hacaton.service;

import org.example.hacaton.dto.request.CreateTeamRequest;
import org.example.hacaton.dto.response.TeamCreateResponse;
import org.example.hacaton.dto.response.TeamInfoResponse;
import org.example.hacaton.dto.response.TeamStatusResponse;
import org.example.hacaton.model.hackathon.Team;

import java.util.List;

public interface TeamService {

    TeamCreateResponse createTeam(CreateTeamRequest request, String token);

    Team findTeamById(Long id);

    void approveTeam(Long teamId, Long managerId);

    void approveMember(Long teamId, Long memberId);

    List<TeamStatusResponse> getTeamsByPendingStatus();

    List<TeamStatusResponse> getTeamsByAcceptedStatus();

    void declineTeam(Long teamId, Long managerId);

    void declineMember(Long teamId, Long memberId, Long managerId);

    TeamInfoResponse getTeamInfoByMemberId(Long userId);
}
