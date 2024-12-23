package org.example.hacaton.serviceTest;

import jakarta.persistence.EntityNotFoundException;
import org.example.hacaton.model.hackathon.Hackathon;
import org.example.hacaton.model.user.Admin;
import org.example.hacaton.model.user.User;
import org.example.hacaton.repository.AdminRepository;
import org.example.hacaton.repository.HackathonRepository;
import org.example.hacaton.repository.MemberRepository;
import org.example.hacaton.repository.TeamRepository;
import org.example.hacaton.service.impl.AdminServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminServiceImplTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private HackathonRepository hackathonRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private AdminServiceImpl adminService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createAdmin_Success() {
        // Arrange
        User user = new User(); // Предположим, у User есть геттеры/сеттеры
        user.setId(1L);
        Admin admin = Admin.builder()
                .user(user)
                .build();

        when(adminRepository.save(any(Admin.class))).thenReturn(admin);

        // Act
        Admin createdAdmin = adminService.createAdmin(user);

        // Assert
        assertNotNull(createdAdmin);
        assertEquals(user, createdAdmin.getUser());
        verify(adminRepository, times(1)).save(any(Admin.class));
    }

    @Test
    void deleteHackathon_Success() {
        // Arrange
        Long hackathonId = 1L;
        Hackathon hackathon = new Hackathon();
        hackathon.setId(hackathonId);

        when(hackathonRepository.findById(hackathonId)).thenReturn(Optional.of(hackathon));

        // Act
        assertDoesNotThrow(() -> adminService.deleteHackathon(hackathonId));

        // Assert
        verify(hackathonRepository, times(1)).findById(hackathonId);
        verify(hackathonRepository, times(1)).delete(hackathon);
    }

    @Test
    void deleteHackathon_NotFound() {
        // Arrange
        Long hackathonId = 1L;

        when(hackathonRepository.findById(hackathonId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> adminService.deleteHackathon(hackathonId));

        assertEquals("Hackathon with id " + hackathonId + " not found", exception.getMessage());
        verify(hackathonRepository, times(1)).findById(hackathonId);
        verify(hackathonRepository, never()).delete(any(Hackathon.class));
    }
}