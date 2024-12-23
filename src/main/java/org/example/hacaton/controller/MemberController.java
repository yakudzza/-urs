package org.example.hacaton.controller;

import lombok.RequiredArgsConstructor;
import org.example.hacaton.security.jwt.JwtService;
import org.example.hacaton.service.MemberService;
import org.example.hacaton.service.TeamService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final JwtService jwtService;
    private final TeamService teamService;

    @PostMapping("/request-join")
    @PreAuthorize("hasAuthority('MEMBER')")
    public ResponseEntity<String> sendRequestToJoinTeam(
            @RequestParam Long teamId,
            @RequestHeader("Authorization") String authHeader
    ) {
        try {
            // Извлекаем userId из токена
            String token = authHeader.substring(7);
            Long userId = jwtService.extractUserId(token);

            // Получаем memberId на основе userId
            Long memberId = memberService.getMemberIdFromUserId(userId);

            // Вызываем метод для отправки запроса на вступление
            memberService.sendRequestToJoinTeam(memberId, teamId);

            return ResponseEntity.ok("Запрос на вступление в команду успешно отправлен!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Ошибка при отправке запроса на вступление: " + e.getMessage());
        }
    }

    @PostMapping("/approve-member")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<String> approveMember(
            @RequestParam Long teamId,
            @RequestParam Long memberId,
            @RequestHeader("Authorization") String authHeader
    ) {
        try {
            String token = authHeader.substring(7);
            Long managerId = jwtService.extractUserId(token);

            teamService.approveMember(teamId, memberId);

            return ResponseEntity.ok("Участник успешно добавлен в команду!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Ошибка при добавлении участника в команду: " + e.getMessage());
        }
    }

    @PostMapping("/decline-member")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<String> declineMember(
            @RequestParam Long teamId,
            @RequestParam Long memberId,
            @RequestHeader("Authorization") String authHeader
    ) {
        try {
            String token = authHeader.substring(7);
            Long managerId = jwtService.extractUserId(token);

            teamService.declineMember(teamId, memberId, managerId);

            return ResponseEntity.ok("Участник успешно не допусщен!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Ошибка при добавлении участника в команду: " + e.getMessage());
        }
    }

}