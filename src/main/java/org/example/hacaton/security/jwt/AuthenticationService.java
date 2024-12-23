package org.example.hacaton.security.jwt;

import lombok.RequiredArgsConstructor;
import org.example.hacaton.dto.request.AuthRequest;
import org.example.hacaton.dto.request.RegisterRequest;
import org.example.hacaton.dto.response.JwtAuthenticationResponse;
import org.example.hacaton.model.user.Role;
import org.example.hacaton.model.user.User;
import org.example.hacaton.service.AdminService;
import org.example.hacaton.service.ManagerService;
import org.example.hacaton.service.MemberService;
import org.example.hacaton.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final MemberService memberService;
    private final ManagerService managerService;
    private final AdminService adminService;


    public JwtAuthenticationResponse signUp(RegisterRequest registerRequest) {
        var user = User.builder()
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .phoneNumber(registerRequest.getPhoneNumber())
                .firstname(registerRequest.getFirstname())
                .lastname(registerRequest.getLastname())
                .patronymic(registerRequest.getPatronymic())
                .role(Role.MEMBER)
                .additionalInfo(registerRequest.getAdditionalInfo())
                .build();

        userService.create(user);

        memberService.createMember(user, registerRequest.getTypeDev());

        var jwt = jwtService.generateToken(user);
        return new JwtAuthenticationResponse(jwt);
    }

    public JwtAuthenticationResponse signUpManager(RegisterRequest registerRequest) {
        var user = User.builder()
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .phoneNumber(registerRequest.getPhoneNumber())
                .firstname(registerRequest.getFirstname())
                .lastname(registerRequest.getLastname())
                .patronymic(registerRequest.getPatronymic())
                .role(Role.MANAGER)
                .additionalInfo(registerRequest.getAdditionalInfo())
                .build();

        userService.create(user);

        managerService.createManager(user);

        var jwt = jwtService.generateToken(user);
        return new JwtAuthenticationResponse(jwt);
    }

    /**
     * Аутентификация пользователя
     *
     * @param request данные пользователя
     * @return токен
     */
    public JwtAuthenticationResponse signIn(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = userService.findByEmail(request.getEmail());

        var jwt = jwtService.generateToken(user);
        return new JwtAuthenticationResponse(jwt);
    }
}