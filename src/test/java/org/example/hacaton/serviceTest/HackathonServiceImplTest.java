package org.example.hacaton.serviceTest;


import jakarta.persistence.EntityNotFoundException;
import org.example.hacaton.dto.request.CreateHackathonRequest;
import org.example.hacaton.dto.response.HackathonListResponse;
import org.example.hacaton.dto.response.HackathonResponse;
import org.example.hacaton.exception.CustomException;
import org.example.hacaton.mapper.HackathonMapper;
import org.example.hacaton.model.hackathon.Hackathon;
import org.example.hacaton.model.hackathon.State;
import org.example.hacaton.model.user.Admin;
import org.example.hacaton.model.user.Manager;
import org.example.hacaton.repository.AdminRepository;
import org.example.hacaton.repository.HackathonRepository;
import org.example.hacaton.repository.ManagerRepository;
import org.example.hacaton.security.jwt.JwtService;
import org.example.hacaton.service.impl.HackathonServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HackathonServiceImplTest {

    @Mock
    private HackathonRepository hackathonRepository;

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private HackathonMapper hackathonMapper;

    @Mock
    private ManagerRepository managerRepository;

    @InjectMocks
    private HackathonServiceImpl hackathonService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createHackathon_Success() {
        // Arrange
        String token = "dummy-token";
        Long userId = 1L;

        CreateHackathonRequest request = CreateHackathonRequest.builder()
                .title("Hackathon 1")
                .description("Description")
                .startDate(null)
                .endDate(null)
                .managerId(2L)
                .maxTeams(5)
                .build();

        Admin admin = new Admin();
        admin.setId(userId);

        Manager manager = new Manager();
        manager.setId(2L);

        Hackathon hackathon = Hackathon.builder()
                .title(request.getTitle())
                .build();

        when(jwtService.extractUserId(token)).thenReturn(userId);
        when(adminRepository.findById(userId)).thenReturn(Optional.of(admin));
        when(managerRepository.findById(request.getManagerId())).thenReturn(Optional.of(manager));
        when(hackathonRepository.existsByTitle(request.getTitle())).thenReturn(false);
        when(hackathonRepository.save(any(Hackathon.class))).thenReturn(hackathon);
        when(hackathonMapper.toHackathonResponse(hackathon)).thenReturn(new HackathonResponse());

        // Act
        HackathonResponse response = hackathonService.createHackathon(request, token);

        // Assert
        assertNotNull(response);
        verify(jwtService).extractUserId(token);
        verify(adminRepository).findById(userId);
        verify(managerRepository).findById(request.getManagerId());
        verify(hackathonRepository).existsByTitle(request.getTitle());
        verify(hackathonRepository).save(any(Hackathon.class));
    }


    @Test
    void getAllHackathon_ReturnsListOfHackathons() {
        // Arrange
        Hackathon hackathon = new Hackathon();
        hackathon.setId(1L);

        when(hackathonRepository.findAll()).thenReturn(List.of(hackathon));
        when(hackathonMapper.toHackathonListResponse(hackathon)).thenReturn(new HackathonListResponse());

        // Act
        List<HackathonListResponse> responses = hackathonService.getAllHackathon();

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        verify(hackathonRepository).findAll();
    }

    @Test
    void getManagerInHackathon_Success() {
        // Arrange
        Long hackathonId = 1L;
        Manager manager = new Manager();
        manager.setId(2L);

        Hackathon hackathon = new Hackathon();
        hackathon.setManager(manager);

        when(hackathonRepository.findById(hackathonId)).thenReturn(Optional.of(hackathon));

        // Act
        Manager result = hackathonService.getManagerInHackathon(hackathonId);

        // Assert
        assertNotNull(result);
        assertEquals(manager, result);
        verify(hackathonRepository).findById(hackathonId);
    }

    @Test
    void getManagerInHackathon_ThrowsExceptionWhenNoManager() {
        // Arrange
        Long hackathonId = 1L;

        Hackathon hackathon = new Hackathon();
        hackathon.setManager(null);

        when(hackathonRepository.findById(hackathonId)).thenReturn(Optional.of(hackathon));

        // Act & Assert
        Exception exception = assertThrows(IllegalStateException.class, () ->
                hackathonService.getManagerInHackathon(hackathonId)
        );

        assertEquals("Hackathon with id " + hackathonId + " does not have a manager assigned", exception.getMessage());
        verify(hackathonRepository).findById(hackathonId);
    }

    @Test
    void updateHackathonState_Success() {
        // Arrange
        Long hackathonId = 1L;
        String newState = "COMPLETED";

        Hackathon hackathon = new Hackathon();
        hackathon.setId(hackathonId);

        when(hackathonRepository.findById(hackathonId)).thenReturn(Optional.of(hackathon));

        // Act
        hackathonService.updateHackathonState(hackathonId, newState);

        // Assert
        assertEquals(State.COMPLETED, hackathon.getState());
        verify(hackathonRepository).save(hackathon);
    }
}
