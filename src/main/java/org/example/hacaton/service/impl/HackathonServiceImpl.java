package org.example.hacaton.service.impl;


import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hacaton.ai.HackathonStickerGenerator;
import org.example.hacaton.dto.request.CreateHackathonRequest;
import org.example.hacaton.dto.response.HackathonListResponse;
import org.example.hacaton.dto.response.HackathonResponse;
import org.example.hacaton.exception.CustomException;
import org.example.hacaton.mapper.HackathonMapper;
import org.example.hacaton.model.hackathon.Hackathon;
import org.example.hacaton.model.hackathon.State;
import org.example.hacaton.model.user.Admin;
import org.example.hacaton.model.user.Manager;
import org.example.hacaton.repository.AdminRepository;
import org.example.hacaton.repository.HackathonRepository;
import org.example.hacaton.repository.ManagerRepository;
import org.example.hacaton.security.jwt.JwtService;
import org.example.hacaton.service.HackathonService;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;


import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class HackathonServiceImpl implements HackathonService {

    private final HackathonRepository hackathonRepository;
    private final AdminRepository adminRepository;
    private final JwtService jwtService;
    private final HackathonMapper hackathonMapper;
    private final ManagerRepository managerRepository;

    @Transactional
    @Override
    public HackathonResponse createHackathon(CreateHackathonRequest createHackathonRequest, String token) {
        try {
            Long userId = jwtService.extractUserId(token);
            log.info("Creating hackathon for admin with ID: {}", userId);

            // Получаем администратора, который пытается создать хакатон
            Admin admin = adminRepository.findById(userId)
                    .orElseThrow(() -> {
                        log.error("Admin with userId {} not found", userId);
                        return new AccessDeniedException("Only admins can create hackathons");
                    });

            // Проверка на уникальность имени хакатона
            if (hackathonRepository.existsByTitle(createHackathonRequest.getTitle())) {
                log.error("Hackathon with title '{}' already exists", createHackathonRequest.getTitle());
                throw new IllegalArgumentException("Hackathon with this title already exists");
            }

            var manager = managerRepository.findById(createHackathonRequest.getManagerId())
                    .orElseThrow(() -> new CustomException("Менеджера с таким id не существует"));

            // Создание хакатона
            Hackathon hackathon = Hackathon.builder()
                    .title(createHackathonRequest.getTitle())
                    .description(createHackathonRequest.getDescription())
                    .startDate(createHackathonRequest.getStartDate())
                    .endDate(createHackathonRequest.getEndDate())
                    .creator(admin)
                    .manager(manager)
                    .maxTeams(createHackathonRequest.getMaxTeams())
                    .state(State.ANNOUNCED)
                    .fullDescription(createHackathonRequest.getFullDescription())
                    .build();

            Hackathon savedHackathon = hackathonRepository.save(hackathon);

            return hackathonMapper.toHackathonResponse(savedHackathon);

        } catch (Exception e) {
            log.error("Error creating hackathon: {}", e.getMessage());
            throw new RuntimeException("Error while creating hackathon", e);
        }
    }


    @Transactional
    @Override
    public List<HackathonListResponse> getAllHackathon() {
        List<Hackathon> hackathons = hackathonRepository.findAll();
        return hackathons.stream()
                .map(hackathonMapper::toHackathonListResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Manager getManagerInHackathon(Long id) {
        var hck = hackathonRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Hackathon with id " + id + " not found"));

        Manager manager = hck.getManager();
        if (manager == null) {
            throw new IllegalStateException("Hackathon with id " + id + " does not have a manager assigned");
        }

        return manager;
    }

    @Override
    @Transactional
    public HackathonResponse getHackathonById(Long id){
        var hackathon = hackathonRepository.findById(id)
                .orElseThrow(() -> new CustomException("Хакатона с таким id не существует"));
        return hackathonMapper.toHackathonResponse(hackathon);
    }

    @Override
    @Transactional
    public Hackathon addManagerToHackathon(Long hackathonId, Long managerId) {
        // Находим хакатон по ID
        Hackathon hackathon = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new EntityNotFoundException("Hackathon with id " + hackathonId + " not found"));

        // Находим менеджера по ID
        Manager manager = managerRepository.findById(managerId)
                .orElseThrow(() -> new EntityNotFoundException("Manager with id " + managerId + " not found"));

        // Присваиваем менеджера хакатону
        hackathon.setManager(manager);

        // Сохраняем изменения в хакатоне
        return hackathonRepository.save(hackathon);
    }

    @Override
    @Transactional
    public void updateHackathonState(Long hackathonId, String newState) {
        // Проверяем, существует ли хакатон с указанным ID
        Hackathon hackathon = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new EntityNotFoundException("Hackathon with id " + hackathonId + " not found"));

        // Проверяем, является ли новое состояние валидным
        try {
            State state = State.valueOf(newState);
            hackathon.setState(state);
            hackathonRepository.save(hackathon); // Сохраняем изменения
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid state: " + newState);
        }
    }

    public void createHackathonSticker(String name, String description) {
        try {
            // Получаем экземпляр генератора стикеров
            HackathonStickerGenerator generator = HackathonStickerGenerator.getInstance();

            // Генерируем стикер
            String stickerPath = generator.generateHackathonSticker(name, description);

            System.out.println("Стикер для хакатона создан и сохранен: " + stickerPath);
        } catch (IOException e) {
            System.err.println("Ошибка при создании стикера для хакатона: " + e.getMessage());
            e.printStackTrace();
        }
    }

}

