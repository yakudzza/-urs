package org.example.hacaton.ControllerTest;


import org.example.hacaton.controller.AdminController;
import org.example.hacaton.service.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import jakarta.persistence.EntityNotFoundException;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AdminControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AdminService adminService;

    @InjectMocks
    private AdminController adminController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(adminController).build();
    }

    @Test
    void deleteHackathon_Success() throws Exception {
        // Arrange
        Long hackathonId = 1L;

        // Act & Assert
        mockMvc.perform(delete("/api/admins/{id}", hackathonId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Hackathon with id " + hackathonId + " has been deleted."));

        verify(adminService, times(1)).deleteHackathon(hackathonId);
    }

    @Test
    void deleteHackathon_NotFound() throws Exception {
        // Arrange
        Long hackathonId = 1L;
        doThrow(new EntityNotFoundException("Hackathon not found")).when(adminService).deleteHackathon(hackathonId);

        // Act & Assert
        mockMvc.perform(delete("/api/admins/{id}", hackathonId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Hackathon with id " + hackathonId + " not found."));

        verify(adminService, times(1)).deleteHackathon(hackathonId);
    }

    @Test
    void deleteHackathon_InternalServerError() throws Exception {
        // Arrange
        Long hackathonId = 1L;
        doThrow(new RuntimeException("Unexpected error")).when(adminService).deleteHackathon(hackathonId);

        // Act & Assert
        mockMvc.perform(delete("/api/admins/{id}", hackathonId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error deleting hackathon: Unexpected error"));

        verify(adminService, times(1)).deleteHackathon(hackathonId);
    }
}