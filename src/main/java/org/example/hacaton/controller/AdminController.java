package org.example.hacaton.controller;


import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.hacaton.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admins")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteHackathon(@PathVariable Long id) {
        try {
            adminService.deleteHackathon(id);
            return ResponseEntity.ok("Hackathon with id " + id + " has been deleted.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body("Hackathon with id " + id + " not found.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting hackathon: " + e.getMessage());
        }
    }
    


}
