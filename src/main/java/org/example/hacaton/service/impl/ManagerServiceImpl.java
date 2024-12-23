package org.example.hacaton.service.impl;


import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.hacaton.dto.response.ManagerListResponse;
import org.example.hacaton.mapper.ManagerMapper;
import org.example.hacaton.model.user.Manager;
import org.example.hacaton.model.user.User;
import org.example.hacaton.repository.ManagerRepository;
import org.example.hacaton.repository.MemberRepository;
import org.example.hacaton.repository.TeamRepository;
import org.example.hacaton.repository.UserRepository;
import org.example.hacaton.service.ManagerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ManagerServiceImpl implements ManagerService {

    private final ManagerRepository managerRepository;
    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final ManagerMapper managerMapper;

    @Override
    @Transactional
    public Manager createManager(User user){
        var manager = Manager.builder()
                .user(user)
                .build();

        return managerRepository.save(manager);
    }

    @Override
    @Transactional
    public Manager findManagerById(Long id) {
        return managerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Менеджера с таким id не существует"));
    }

    @Override
    @Transactional
    public List<Manager> findManagersInHackathonById(Long id) {
        return managerRepository.findManagersInHackathonById(id);
    }


    @Transactional
    @Override
    public void deleteMemberFromTeam(Long memberId, Long teamId) {
        var team = teamRepository.findById(teamId)
                .orElseThrow(() -> new EntityNotFoundException("Team with id " + teamId + " not found"));

        var member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Member with id " + memberId + " not found"));

        // Удаляем участника из команды
        if (!team.getMembers().remove(member)) {
            throw new IllegalStateException("Member is not part of this team");
        }

        // Сохраняем обновленную команду
        teamRepository.save(team);
    }

    @Transactional
    @Override
    public void deleteMemberAndUser(Long memberId, Long userId) {
        // Находим участника по ID
        var member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Member with id " + memberId + " not found"));

        // Находим пользователя по ID
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));

        // Проверяем, есть ли пользователь, связанный с участником
        if (!member.getUser().equals(user)) {
            throw new IllegalStateException("User ID does not match the member");
        }

        // Удаляем участника
        memberRepository.delete(member);

        // Удаляем пользователя
        userRepository.delete(user);
    }


    @Override
    @Transactional
    public List<ManagerListResponse> getAllManagers() {
        List<Manager> managers = managerRepository.findAll();

        // Используем маппер для преобразования
        return managers.stream()
                .map(managerMapper::toManagerListResponse)
                .collect(Collectors.toList());
    }

}

