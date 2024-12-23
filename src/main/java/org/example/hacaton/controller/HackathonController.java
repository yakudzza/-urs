package org.example.hacaton.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hacaton.dto.request.CreateHackathonRequest;
import org.example.hacaton.dto.request.UpdateHackathonStateRequest;
import org.example.hacaton.dto.response.HackathonListResponse;
import org.example.hacaton.dto.response.HackathonResponse;
import org.example.hacaton.model.hackathon.Hackathon;
import org.example.hacaton.service.HackathonService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/hackathon")
public class HackathonController {

    private final HackathonService hackathonService;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Создание хакатона", description = "Создает новый хакатон (только для админов)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Хакатон успешно создан"),
            @ApiResponse(responseCode = "403", description = "Нет прав доступа"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public ResponseEntity<HackathonResponse> createHackathon(
            @Valid @RequestBody CreateHackathonRequest createHackathonRequest,
            @RequestHeader("Authorization") String authHeader) {

        log.info("Received request to create hackathon");
        String token = authHeader.substring(7);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("User authorities: {}", authentication.getAuthorities());

        HackathonResponse hackathonResponse = hackathonService.createHackathon(createHackathonRequest, token);
        return ResponseEntity.ok(hackathonResponse);
    }



    @PostMapping("/createSticker")
    public String createHackathon(@RequestParam String name, @RequestParam String description) {
        hackathonService.createHackathonSticker(name, description);
        return "Стикер для хакатона создан!";
    }


    @GetMapping("/getAllHackathon")
    public ResponseEntity<List<HackathonListResponse>> getAllHackathon() {
        List<HackathonListResponse> hackathons = hackathonService.getAllHackathon();
        return ResponseEntity.ok(hackathons);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Получить информацию о хакатоне",
            description = "Возвращает полную информацию о хакатоне по его ID"
    )
    public ResponseEntity<HackathonResponse> getHackathonById(@PathVariable Long id) {
        HackathonResponse response = hackathonService.getHackathonById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{hackathonId}/manager/{managerId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> addManagerToHackathon(@PathVariable Long hackathonId, @PathVariable Long managerId) {
        try {
            // Добавляем менеджера к хакатону
            Hackathon updatedHackathon = hackathonService.addManagerToHackathon(hackathonId, managerId);
            String update = "Вы успешно добавили менеджера к хакатону";
            return ResponseEntity.ok(update);
        } catch (EntityNotFoundException e) {
            // Возвращаем ошибку, если хакатон или менеджер не найдены
            return ResponseEntity.status(404).body(null);
        } catch (Exception e) {
            // Возвращаем общую ошибку в случае других исключений
            return ResponseEntity.status(500).body(null);
        }
    }


    @PutMapping("/update-state")
    public ResponseEntity<String> updateHackathonState(@RequestBody UpdateHackathonStateRequest request) {
        try {
            hackathonService.updateHackathonState(request.getHackathonId(), request.getNewState());
            return ResponseEntity.ok("Hackathon state updated successfully");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
