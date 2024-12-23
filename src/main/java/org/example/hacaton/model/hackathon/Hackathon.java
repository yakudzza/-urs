package org.example.hacaton.model.hackathon;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.example.hacaton.model.user.Admin;
import org.example.hacaton.model.user.Manager;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "hackathons")
public class Hackathon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String fullDescription;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    private Admin creator;

    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private State state;

    @ManyToOne
    @JoinColumn(name = "manager_id", nullable = true)  // может быть null, если не назначен
    @JsonBackReference
    private Manager manager;  // Менеджер хакатона

    @Column(name = "teams_allowed")
    private boolean teamsAllowed = false;

    @Column(name = "team_size")
    private int teamSize;

    @Column(name = "max_teams")
    private Integer maxTeams;

    @Column(name = "photo_url")
    private String photoUrl;

    @ManyToMany
    @JoinTable(
            name = "admins_hackathons",
            joinColumns = @JoinColumn(name = "hackathon_id"),
            inverseJoinColumns = @JoinColumn(name = "admin_id")
    )
    private Set<Admin> admins = new HashSet<>();
}