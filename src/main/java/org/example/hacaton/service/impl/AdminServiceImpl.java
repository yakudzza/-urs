package org.example.hacaton.service.impl;


import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.hacaton.model.hackathon.Hackathon;
import org.example.hacaton.model.user.Admin;
import org.example.hacaton.model.user.User;
import org.example.hacaton.repository.AdminRepository;
import org.example.hacaton.repository.HackathonRepository;
import org.example.hacaton.service.AdminService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final HackathonRepository hackathonRepository;

    @Override
    public Admin createAdmin(User user) {
        var admin = Admin.builder()
                .user(user)
                .build();
        return adminRepository.save(admin);
    }

    @Transactional
    @Override
    public void deleteHackathon(Long id) {
        Hackathon hackathon = hackathonRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Hackathon with id " + id + " not found"));

        hackathonRepository.delete(hackathon);
    }
}
