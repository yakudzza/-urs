package org.example.hacaton.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hacaton.dto.request.CreateTeamRequest;
import org.example.hacaton.dto.response.TeamCreateResponse;
import org.example.hacaton.dto.response.TeamInfoResponse;
import org.example.hacaton.dto.response.TeamStatusResponse;
import org.example.hacaton.exception.CustomException;
import org.example.hacaton.mapper.TeamMapToResponse;
import org.example.hacaton.model.hackathon.Hackathon;
import org.example.hacaton.model.hackathon.Team;
import org.example.hacaton.model.user.*;
import org.example.hacaton.repository.*;
import org.example.hacaton.security.jwt.JwtService;
import org.example.hacaton.service.NotificationService;
import org.example.hacaton.service.TeamService;
import org.example.hacaton.service.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final HackathonRepository hackathonRepository;
    private final UserService userService;
    private final JwtService jwtService;
    private final NotificationService notificationService;
    private final TeamMapToResponse teamMapToResponse;
    private final MemberRepository memberRepository;
    private final ManagerRepository managerRepository;
    private final NotificationRepository notificationRepository;


    @Override
    @Transactional
    public TeamCreateResponse createTeam(CreateTeamRequest request, String token) {
        Long userId = jwtService.extractUserId(token);
        log.info("Creating team request for user with ID: {}", userId);

        // Находим пользователя-создателя
        User creator = userService.getUserById(userId);
        log.debug("Team creator retrieved: {}", creator.getId());

        // Получаем хакатон
        Hackathon hackathon = hackathonRepository.findByTitle(request.getHackathonName());
        if (hackathon == null) {
            throw new RuntimeException("Hackathon not found with name: " + request.getHackathonName());
        }

        // Проверяем возможность создания команды
        if (!canWeCreateTeam(hackathon)) {
            throw new RuntimeException("Cannot create a team for the specified hackathon. Please check the conditions.");
        }

        // Проверяем, существует ли менеджер у хакатона
        Manager manager = managerRepository.findById(hackathon.getManager().getId())
                .orElseThrow(() -> new RuntimeException("No manager assigned to the hackathon"));

        // Создаем команду со статусом PENDING
        Team team = Team.builder()
                .name(request.getName())
                .hackathon(hackathon)
                .status(Status.PENDING) // команда ожидает одобрения
                .creator(creator)
                .manager(manager)
                .members(new HashSet<>())
                .build();

        // Добавляем создателя команды как участника
        team.getMembers().add(creator);

        // Сохраняем команду
        Team savedTeam = teamRepository.save(team);

        // Уведомляем менеджера о новой заявке
        createNotificationForManager(savedTeam, manager);

        log.info("Team '{}' created successfully with ID: {}", team.getName(), team.getId());

        // Возвращаем результат
        return teamMapToResponse.toTeamCreateResponse(savedTeam);
    }

    @Transactional
    public boolean canWeCreateTeam(Hackathon hackathon) {
        if (hackathon.getTeamSize() > hackathon.getMaxTeams()) {
            log.warn("Команды не могут быть созданы для хакатона '{}'", hackathon.getTitle());
            return false;
        }


        // Проверяем, есть ли менеджер у хакатона
        if (hackathon.getManager() == null) {
            log.warn("У хакатона '{}' нет назначенного менеджера", hackathon.getTitle());
            return false;
        }

        // Проверяем количество команд в хакатоне
        long teamCount = teamRepository.countByHackathon(hackathon);
        if (hackathon.getMaxTeams() != null && teamCount >= hackathon.getMaxTeams()) {
            log.warn("Хакатон '{}' достиг максимального количества команд", hackathon.getTitle());
            return false;
        }

        return true;
    }


    private void createNotificationForManager(Team team, Manager manager) {
        String notificationText = String.format(
                "Новая команда '%s' запросила регистрацию в хакатоне '%s'.",
                team.getName(),
                team.getHackathon().getTitle()
        );

        Notification notification = Notification.builder()
                .text(notificationText)
                .manager(manager)
                .team(team)
                .status(NotificationStatus.UNREAD)
                .createdAt(LocalDateTime.now())
                .wereFrom(WereFrom.TEAM)
                .build();

        notificationService.save(notification);
    }

    @Override
    @Transactional
    public void approveTeam(Long teamId, Long managerId) {
        // Находим команду по ID
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Команда с таким ID не найдена"));

        // Удаляем уведомления, связанные с этой командой
        notificationRepository.deleteByTeam(team);

        // Обновляем статус команды на APPROVED
        team.setStatus(Status.ACCEPTED);
        teamRepository.save(team);

        // Обновляем информацию о командах в хакатоне
        Hackathon hackathon = team.getHackathon();
        hackathon.setTeamSize((hackathon.getTeamSize() != 0 ? hackathon.getTeamSize() : 0) + 1);
        hackathonRepository.save(hackathon);

        log.info("Team '{}' has been approved and added to hackathon '{}'.", team.getName(), hackathon.getTitle());
    }

    @Override
    @Transactional
    public void declineTeam(Long teamId, Long managerId) {
        // Находим команду по ID
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Команда с таким ID не найдена"));

        // Удаляем уведомления, связанные с этой командой
        notificationRepository.deleteByTeam(team);

        // Удаляем команду
        teamRepository.delete(team);
    }

    @Override
    @Transactional
    public Team findTeamById(Long id){
        return teamRepository.findById(id)
                .orElseThrow(() -> new CustomException("Команды с таким id не существует"));
    }

    @Override
    @Transactional
    public void approveMember(Long teamId, Long memberId) {
        // Находим команду по ID
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Команда с таким ID не найдена"));

        // Находим участника по ID
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Участник с таким ID не найден"));

        // Проверяем, не состоит ли участник уже в команде
        if (team.getMembers().contains(member)) {
            throw new RuntimeException("Этот участник уже состоит в команде");
        }

        // Удаляем уведомления, связанные с данным участником и командой
        notificationRepository.deleteByTeamAndMember(team, member);

        // Добавляем участника в команду
        team.getMembers().add(member.getUser());
        teamRepository.save(team);
    }

    @Override
    @Transactional
    public void declineMember(Long teamId, Long memberId, Long managerId) {
        // Находим команду по ID
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Команда с таким ID не найдена"));

        // Находим участника по ID
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Участник с таким ID не найден"));

        // Удаляем уведомления, связанные с данным участником и командой
        notificationRepository.deleteByTeamAndMember(team, member);

        // Уведомления удалены, дополнительное действие не требуется
    }


    @Override
    @Transactional
    public TeamInfoResponse getTeamInfoByMemberId(Long memberId) {
        // Ищем мембера по ID
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Участник с таким ID не найден"));

        // Используем userId мембера для поиска команды
        Long userId = member.getUser().getId();
        Team team = teamRepository.findByUserIdInTeams(userId)
                .orElseThrow(() -> new RuntimeException("Участник не состоит ни в одной команде."));

        // Возвращаем информацию о команде и хакатоне
        return new TeamInfoResponse(
                team.getName(),
                team.getHackathon().getTitle()
        );
    }


    @Override
    @Transactional
    public List<TeamStatusResponse> getTeamsByPendingStatus() {
        return teamRepository.findAll().stream()
                .filter(team -> team.getStatus() == Status.PENDING) // Фильтруем команды со статусом PENDING
                .map(teamMapToResponse::toTeamStatusResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<TeamStatusResponse> getTeamsByAcceptedStatus() {
        return teamRepository.findAll().stream()
                .filter(team -> team.getStatus() == Status.ACCEPTED) // Фильтруем команды со статусом ACCEPTED
                .map(teamMapToResponse::toTeamStatusResponse)
                .collect(Collectors.toList());
    }


}