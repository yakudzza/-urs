package org.example.hacaton.controller;


import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.hacaton.dto.response.ManagerListResponse;
import org.example.hacaton.security.jwt.JwtService;
import org.example.hacaton.service.ManagerService;
import org.example.hacaton.service.TeamService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/managers")
@RequiredArgsConstructor
public class ManagerController {

    private final ManagerService managerService;
    private final JwtService jwtService;
    private final TeamService teamService;

    @GetMapping("/getAllManagers")
    public ResponseEntity<List<ManagerListResponse>> getAllManagers() {
        List<ManagerListResponse> managers = managerService.getAllManagers();
        return ResponseEntity.ok(managers);
    }

    @PostMapping("/approve")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<String> approveTeam(
            @RequestParam Long teamId,
            @RequestHeader("Authorization") String authHeader
    ) {
        try {
            String token = authHeader.substring(7);
            Long managerId = jwtService.extractUserId(token);

            teamService.approveTeam(teamId, managerId);

            return ResponseEntity.ok("Команда успешно одобрена!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Ошибка при принятии команды: " + e.getMessage());
        }
    }

    @DeleteMapping("/{teamId}/members/{memberId}")
    public ResponseEntity<String> deleteMemberFromTeam(
            @PathVariable Long teamId,
            @PathVariable Long memberId) {
        try {
            managerService.deleteMemberFromTeam(memberId, teamId);
            return ResponseEntity.ok("Member with id " + memberId + " has been removed from team with id " + teamId);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body("Team or Member not found: " + e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(400).body("Error removing member: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error removing member: " + e.getMessage());
        }
    }

    @PostMapping("/declineTeam")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<String> declineTeam(
            @RequestParam Long teamId,
            @RequestHeader("Authorization") String authHeader
    ) {
        try {
            String token = authHeader.substring(7);
            Long managerId = jwtService.extractUserId(token);

            teamService.declineTeam(teamId, managerId);

            return ResponseEntity.ok("Команда успешно удалена");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Ошибка при принятии команды: " + e.getMessage());
        }
    }
}
