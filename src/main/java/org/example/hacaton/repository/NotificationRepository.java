package org.example.hacaton.repository;

import org.example.hacaton.model.hackathon.Team;
import org.example.hacaton.model.user.Member;
import org.example.hacaton.model.user.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByManagerUserIdOrMemberUserId(Long managerUserId, Long memberUserId);

    List<Notification> findAll();

    void deleteByTeam(Team team);

    // Удаление уведомлений по команде и участнику
    void deleteByTeamAndMember(Team team, Member member);

}
