package com.yaskondrichin.ContactsService.controller;



import com.yaskondrichin.ContactsService.DTO.LoginResponseDTO;
import com.yaskondrichin.ContactsService.Mapper.LoginMapper;
import com.yaskondrichin.ContactsService.domain.controller.LoginController;
import com.yaskondrichin.ContactsService.domain.model.Login;
import com.yaskondrichin.ContactsService.domain.model.Role;
import com.yaskondrichin.ContactsService.service.LoginService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Подменяем бин JwtDecoder, созданный в твоем SecurityConfig, чтобы он не ходил в Keycloak
    @MockitoBean
    private JwtDecoder jwtDecoder;

    // Подменяем бизнес-логику контроллера
    @MockitoBean
    private LoginService loginService;

    @MockitoBean
    private LoginMapper loginMapper;

    @Test
    public void getMe_WhenAuthenticated_ShouldReturnOkAndMappedUser() throws Exception {
        Login mockLogin = new Login(10L, "ownerUser", "password", "owner@mail.com", "+375291111111", Role.USER);
        LoginResponseDTO responseDto = new LoginResponseDTO();
        responseDto.setId(10L);
        responseDto.setLogin("ownerUser");

        Mockito.when(loginService.getMe(Mockito.any(Jwt.class))).thenReturn(mockLogin);
        Mockito.when(loginMapper.toResponseDto(mockLogin)).thenReturn(responseDto);

        mockMvc.perform(get("/api/v1/logins/me")
                        .with(jwt().jwt(jwt -> jwt.subject("10"))
                                .authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.login").value("ownerUser"));
    }

    @Test
    public void debug_ShouldReturnCorrectStringWithSubject() throws Exception {
        mockMvc.perform(get("/api/v1/logins/debug")
                        .with(jwt().jwt(jwt -> jwt.subject("custom_sub"))))
                .andExpect(status().isOk())
                .andExpect(content().string("Your ID from token (sub): custom_sub"));
    }

    @Test
    public void getMe_WhenUnauthenticated_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/logins/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}