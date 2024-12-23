package org.example.hacaton.serviceTest;

import org.example.hacaton.dto.request.CreateTeamRequest;
import org.example.hacaton.dto.response.TeamCreateResponse;
import org.example.hacaton.mapper.TeamMapToResponse;
import org.example.hacaton.model.hackathon.Hackathon;
import org.example.hacaton.model.hackathon.Team;
import org.example.hacaton.model.user.Manager;
import org.example.hacaton.model.user.Member;
import org.example.hacaton.model.user.Status;
import org.example.hacaton.model.user.User;
import org.example.hacaton.repository.*;
import org.example.hacaton.security.jwt.JwtService;
import org.example.hacaton.service.NotificationService;
import org.example.hacaton.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.example.hacaton.service.impl.TeamServiceImpl;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TeamServiceImplTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private HackathonRepository hackathonRepository;

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private TeamMapToResponse teamMapToResponse;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ManagerRepository managerRepository;

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private TeamServiceImpl teamService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void createTeam_FailsWhenHackathonNotFound() {
        // Arrange
        String token = "dummyToken";
        Long userId = 1L;

        when(jwtService.extractUserId(token)).thenReturn(userId);

        User creator = new User();
        creator.setId(userId);
        when(userService.getUserById(userId)).thenReturn(creator);

        CreateTeamRequest request = new CreateTeamRequest();
        request.setName("Team Alpha");
        request.setHackathonName("Unknown Hackathon");

        when(hackathonRepository.findByTitle(request.getHackathonName())).thenReturn(null);

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () ->
                teamService.createTeam(request, token));

        assertEquals("Hackathon not found with name: Unknown Hackathon", exception.getMessage());
    }

    @Test
    void approveTeam_Success() {
        // Arrange
        Long teamId = 1L;
        Team team = new Team();
        team.setId(teamId);

        Hackathon hackathon = new Hackathon();
        hackathon.setTeamSize(0);
        team.setHackathon(hackathon);

        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));

        // Act
        teamService.approveTeam(teamId, 1L);

        // Assert
        assertEquals(Status.ACCEPTED, team.getStatus());
        assertEquals(1, hackathon.getTeamSize());
        verify(teamRepository).save(team);
        verify(notificationRepository).deleteByTeam(team);
    }

    @Test
    void approveMember_Success() {
        // Arrange
        Long teamId = 1L;
        Long memberId = 2L;

        Team team = new Team();
        team.setId(teamId);
        team.setMembers(new HashSet<>());

        Member member = new Member();
        member.setId(memberId);
        member.setUser(new User());

        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        // Act
        teamService.approveMember(teamId, memberId);

        // Assert
        assertTrue(team.getMembers().contains(member.getUser()));
        verify(teamRepository).save(team);
        verify(notificationRepository).deleteByTeamAndMember(team, member);
    }

    @Test
    void getTeamInfoByMemberId_Success() {
        // Arrange
        Long memberId = 1L;

        Member member = new Member();
        member.setId(memberId);

        User user = new User();
        user.setId(2L);
        member.setUser(user);

        Team team = new Team();
        team.setName("Team Alpha");
        Hackathon hackathon = new Hackathon();
        hackathon.setTitle("Hackathon 2024");
        team.setHackathon(hackathon);

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(teamRepository.findByUserIdInTeams(user.getId())).thenReturn(Optional.of(team));

        // Act
        var result = teamService.getTeamInfoByMemberId(memberId);

        // Assert
        assertNotNull(result);
        assertEquals("Team Alpha", result.getTeamName());
        assertEquals("Hackathon 2024", result.getHackathonName());
    }
}