package org.example.hacaton.ControllerTest;

import org.example.hacaton.controller.AuthenticationController;
import org.example.hacaton.dto.request.AuthRequest;
import org.example.hacaton.dto.request.RegisterRequest;
import org.example.hacaton.dto.response.JwtAuthenticationResponse;
import org.example.hacaton.security.jwt.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthenticationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AuthenticationController authenticationController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authenticationController).build();
    }

    @Test
    void login_Success() throws Exception {
        AuthRequest request = new AuthRequest("user@mail.com", "password123");
        JwtAuthenticationResponse response = new JwtAuthenticationResponse("fake-jwt-token");

        when(authenticationService.signIn(any(AuthRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"email\": \"user@mail.com\", \"password\": \"password123\" }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake-jwt-token"));

        verify(authenticationService, times(1)).signIn(any(AuthRequest.class));
    }
}