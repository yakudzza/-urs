package org.example.hacaton.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.hacaton.dto.request.AuthRequest;
import org.example.hacaton.dto.request.RegisterRequest;
import org.example.hacaton.dto.response.JwtAuthenticationResponse;
import org.example.hacaton.security.jwt.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {


    private final AuthenticationService authenticationService;

    /**
     * Регистрация нового участника (Manager)
     * @param registerRequest данные для регистрации
     * @return JWT токен
     */
    @PostMapping("/register/manager")
    public ResponseEntity<JwtAuthenticationResponse> registerManager(@RequestBody @Valid RegisterRequest registerRequest){
        JwtAuthenticationResponse response = authenticationService.signUpManager(registerRequest);
        return ResponseEntity.ok(response);
    }


    /**
     * Регистрация нового участника (Member)
     * @param registerRequest данные для регистрации
     * @return JWT токен
     */
    @PostMapping("/register/member")
    public ResponseEntity<JwtAuthenticationResponse> registerMember(@RequestBody @Valid RegisterRequest registerRequest) {
        JwtAuthenticationResponse response = authenticationService.signUp(registerRequest);
        return ResponseEntity.ok(response);
    }



    /**
     * Регистрация нового участника (ADMIN)
     * @param registerRequest данные для регистрации
     * @return JWT токен
     */
    @PostMapping("/register/admin")
    public ResponseEntity<JwtAuthenticationResponse> registerAdmin(@RequestBody @Valid RegisterRequest registerRequest){
        JwtAuthenticationResponse response = authenticationService.signUpManager(registerRequest);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/login")
    public ResponseEntity<JwtAuthenticationResponse> sigIn(@RequestBody @Valid AuthRequest request){
        JwtAuthenticationResponse response = authenticationService.signIn(request);
        return ResponseEntity.ok(response);
    }


}
