package org.example.hacaton.controller;


import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hacaton.dto.request.CreateTeamRequest;
import org.example.hacaton.dto.response.TeamCreateResponse;
import org.example.hacaton.dto.response.TeamInfoResponse;
import org.example.hacaton.dto.response.TeamStatusResponse;
import org.example.hacaton.security.jwt.JwtService;
import org.example.hacaton.service.TeamService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/teams/")
public class TeamController {

    private final TeamService teamService;
    private final JwtService jwtService;

    @PostMapping("/create")
    @Operation(summary = "Создание команды", description = "Создает новую команду (только для авторизованных пользователей)")
    public ResponseEntity<TeamCreateResponse> createTeam(
            @Valid @RequestBody CreateTeamRequest createTeamRequest,
            @RequestHeader("Authorization") String authHeader) {

        log.info("Received request to create team");

        String token = authHeader.substring(7);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("User authorities: {}", authentication.getAuthorities());

        // Вызов сервиса для создания команды и маппинга в TeamCreateResponse
        TeamCreateResponse response = teamService.createTeam(createTeamRequest, token);
        log.info("Team creation successful");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<TeamStatusResponse>> getPendingTeams() {
        List<TeamStatusResponse> pendingTeams = teamService.getTeamsByPendingStatus();
        return ResponseEntity.ok(pendingTeams);
    }

    @GetMapping("/accepted")
    public ResponseEntity<List<TeamStatusResponse>> getAcceptedTeams() {
        List<TeamStatusResponse> acceptedTeams = teamService.getTeamsByAcceptedStatus();
        return ResponseEntity.ok(acceptedTeams);
    }

    @GetMapping("/member-team")
    @PreAuthorize("hasAuthority('MEMBER')")
    public ResponseEntity<TeamInfoResponse> getMemberTeam(@RequestParam Long memberId) {
        try {
            // Получаем информацию о команде по memberId
            TeamInfoResponse response = teamService.getTeamInfoByMemberId(memberId);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new TeamInfoResponse("Ошибка", e.getMessage()));
        }
    }

}
