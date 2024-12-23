package org.example.hacaton.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.hacaton.model.hackathon.Team;
import org.example.hacaton.model.user.*;
import org.example.hacaton.repository.MemberRepository;
import org.example.hacaton.service.MemberService;
import org.example.hacaton.service.NotificationService;
import org.example.hacaton.service.TeamService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final TeamService teamService;
    private final NotificationService notificationService;


    @Override
    @Transactional
    public Member createMember(User user, TypeDev typeDev){
        var member = Member.builder()
                .user(user)
                .role(Role.MEMBER)
                .typeDev(typeDev)
                .build();

        return memberRepository.save(member);
    }

    @Transactional
    public Member findById(Long id){
        return memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователя с такием id не существует"));
    }


    @Override
    @Transactional
    public void sendRequestToJoinTeam(Long memberId, Long teamId) {
        var member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Пользователя с такими id не существует"));

        var team = teamService.findTeamById(teamId);
        var manager = team.getManager();

        createNotificationForManager(member, team, manager);
    }

    @Override
    public Long getMemberIdFromUserId(Long userId) {
        Member member = memberRepository.findByUserId(userId);
        return member.getId(); // Возвращаем memberId
    }

    private void createNotificationForManager(Member member, Team team, Manager manager) {
        String notificationText = String.format(
                "Пользователь '%s %s' запросил вступление в команду '%s' в хакатоне '%s'.",
                member.getUser().getFirstname(),
                member.getUser().getLastname(),
                team.getName(),
                team.getHackathon().getTitle()
        );

        Notification notification = Notification.builder()
                .text(notificationText)
                .manager(manager)
                .team(team)
                .status(NotificationStatus.UNREAD)
                .createdAt(LocalDateTime.now())
                .wereFrom(WereFrom.USER)
                .member(member)
                .build();

        notificationService.save(notification);
    }

}
