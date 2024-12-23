package org.example.hacaton.repository;

import org.example.hacaton.model.hackathon.Hackathon;
import org.example.hacaton.model.hackathon.Team;
import org.example.hacaton.model.user.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    long countByHackathon(Hackathon hackathon);

    boolean existsByMembers(Member user);

    @Query("SELECT t FROM Team t JOIN t.members m WHERE m.id = :userId")
    Optional<Team> findByUserIdInTeams(@Param("userId") Long userId);
}
