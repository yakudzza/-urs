package org.example.hacaton.service;

import org.example.hacaton.dto.response.ManagerListResponse;
import org.example.hacaton.model.user.Manager;
import org.example.hacaton.model.user.User;

import java.util.List;

public interface ManagerService {

    Manager createManager(User user);

    Manager findManagerById(Long id);

    List<Manager> findManagersInHackathonById(Long id);

    void deleteMemberFromTeam(Long memberId, Long teamId);

    void deleteMemberAndUser(Long memberId, Long userId);

    List<ManagerListResponse> getAllManagers();
}
