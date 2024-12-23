package org.example.hacaton.service;

import org.example.hacaton.model.user.Member;
import org.example.hacaton.model.user.TypeDev;
import org.example.hacaton.model.user.User;

public interface MemberService {

    Member createMember(User user, TypeDev typeDev);

    Member findById(Long id);

    void sendRequestToJoinTeam(Long memberId, Long teamId);

    Long getMemberIdFromUserId(Long userId);
}
