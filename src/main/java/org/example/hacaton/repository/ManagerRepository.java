package org.example.hacaton.repository;

import org.example.hacaton.model.user.Manager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ManagerRepository extends JpaRepository<Manager, Long> {

    @Query("SELECT m FROM Manager m WHERE m.id IN (SELECT h.manager.id FROM Hackathon h WHERE h.id = :id)")
    List<Manager> findManagersInHackathonById(Long id);
}
