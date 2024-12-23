package org.example.hacaton.repository;

import org.example.hacaton.model.hackathon.Hackathon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HackathonRepository extends JpaRepository<Hackathon, Long> {

    Optional<Hackathon> findById(Long id);

    Hackathon findByTitle(String  titles);

    boolean existsByTitle(String title);
}

