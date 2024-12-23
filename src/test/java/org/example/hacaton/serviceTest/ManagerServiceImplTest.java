package org.example.hacaton.serviceTest;

import jakarta.persistence.EntityNotFoundException;
import org.example.hacaton.dto.response.ManagerListResponse;
import org.example.hacaton.mapper.ManagerMapper;
import org.example.hacaton.model.user.Manager;
import org.example.hacaton.model.user.Member;
import org.example.hacaton.model.user.User;
import org.example.hacaton.repository.ManagerRepository;
import org.example.hacaton.repository.MemberRepository;
import org.example.hacaton.repository.TeamRepository;
import org.example.hacaton.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.example.hacaton.service.impl.ManagerServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ManagerServiceImplTest {

    @Mock
    private ManagerRepository managerRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ManagerMapper managerMapper;

    @InjectMocks
    private ManagerServiceImpl managerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createManager_Success() {
        // Arrange
        User user = new User();
        user.setId(1L);

        Manager manager = Manager.builder().user(user).build();
        when(managerRepository.save(any(Manager.class))).thenReturn(manager);

        // Act
        Manager createdManager = managerService.createManager(user);

        // Assert
        assertNotNull(createdManager);
        assertEquals(user, createdManager.getUser());
        verify(managerRepository).save(any(Manager.class));
    }

    @Test
    void findManagerById_Success() {
        // Arrange
        Long managerId = 1L;
        Manager manager = new Manager();
        manager.setId(managerId);

        when(managerRepository.findById(managerId)).thenReturn(Optional.of(manager));

        // Act
        Manager foundManager = managerService.findManagerById(managerId);

        // Assert
        assertNotNull(foundManager);
        assertEquals(managerId, foundManager.getId());
        verify(managerRepository).findById(managerId);
    }

    @Test
    void findManagerById_ThrowsException() {
        // Arrange
        Long managerId = 1L;
        when(managerRepository.findById(managerId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () ->
                managerService.findManagerById(managerId)
        );

        assertEquals("Менеджера с таким id не существует", exception.getMessage());
        verify(managerRepository).findById(managerId);
    }


    @Test
    void deleteMemberFromTeam_ThrowsExceptionWhenTeamNotFound() {
        // Arrange
        Long teamId = 2L;

        when(teamRepository.findById(teamId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(EntityNotFoundException.class, () ->
                managerService.deleteMemberFromTeam(1L, teamId)
        );

        assertEquals("Team with id " + teamId + " not found", exception.getMessage());
        verify(teamRepository).findById(teamId);
    }

    @Test
    void deleteMemberAndUser_Success() {
        // Arrange
        Long memberId = 1L;
        Long userId = 2L;

        User user = new User();
        user.setId(userId);

        Member member = new Member();
        member.setId(memberId);
        member.setUser(user);

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        managerService.deleteMemberAndUser(memberId, userId);

        // Assert
        verify(memberRepository).delete(member);
        verify(userRepository).delete(user);
    }

    @Test
    void deleteMemberAndUser_ThrowsExceptionWhenUserMismatch() {
        // Arrange
        Long memberId = 1L;
        Long userId = 2L;

        User user = new User();
        user.setId(userId);

        Member member = new Member();
        member.setId(memberId);
        member.setUser(new User()); // Участник связан с другим пользователем

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act & Assert
        Exception exception = assertThrows(IllegalStateException.class, () ->
                managerService.deleteMemberAndUser(memberId, userId)
        );

        assertEquals("User ID does not match the member", exception.getMessage());
    }

    @Test
    void getAllManagers_Success() {
        // Arrange
        Manager manager = new Manager();
        manager.setId(1L);

        ManagerListResponse response = new ManagerListResponse();
        when(managerRepository.findAll()).thenReturn(List.of(manager));
        when(managerMapper.toManagerListResponse(manager)).thenReturn(response);

        // Act
        List<ManagerListResponse> result = managerService.getAllManagers();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(managerRepository).findAll();
    }
}