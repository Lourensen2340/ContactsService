package com.yaskondrichin.ContactsService.controller;

import com.yaskondrichin.ContactsService.DTO.LoginResponseDTO;
import com.yaskondrichin.ContactsService.Mapper.LoginMapper;
import com.yaskondrichin.ContactsService.config.JwtProvider;
import com.yaskondrichin.ContactsService.controller.config.TestSecurityConfig;
import com.yaskondrichin.ContactsService.domain.controller.LoginController;
import com.yaskondrichin.ContactsService.domain.model.Login;
import com.yaskondrichin.ContactsService.domain.repo.LoginRepository;
import com.yaskondrichin.ContactsService.service.LoginService;
import com.yaskondrichin.ContactsService.service.impl.JwtServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoginController.class)
@Import({JwtProvider.class, TestSecurityConfig.class})
public class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LoginService loginService;

    @MockitoBean
    private LoginMapper loginMapper;

    @MockitoBean
    private LoginRepository loginRepository;

    @MockitoBean
    private JwtServiceImpl jwtService;

    @MockitoBean
    private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    @MockitoBean
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    private final UUID mockUserId = UUID.randomUUID();

    @Test
    public void getMe_WhenAuthenticated_ShouldReturnOkAndMappedUser() throws Exception {
        Login mockLogin = Mockito.mock(Login.class);
        LoginResponseDTO responseDto = new LoginResponseDTO();
        responseDto.setId(mockUserId); // Теперь ID это UUID
        responseDto.setLogin("ownerUser");

        when(loginService.getMe(Mockito.any(Jwt.class))).thenReturn(mockLogin);
        when(loginMapper.toResponseDto(mockLogin)).thenReturn(responseDto);

        mockMvc.perform(get("/api/v1/logins/me")
                        .with(jwt().jwt(jwt -> jwt.subject(mockUserId.toString()))
                                .authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockUserId.toString()))
                .andExpect(jsonPath("$.login").value("ownerUser"));
    }

    @Test
    public void delete_WhenUserExists_ShouldReturnNoContent() throws Exception {
        Login mockLogin = Mockito.mock(Login.class);
        when(loginRepository.findById(mockUserId)).thenReturn(Optional.of(mockLogin));
        doNothing().when(loginRepository).delete(mockLogin);

        mockMvc.perform(delete("/api/v1/logins/" + mockUserId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .with(csrf()))
                .andExpect(status().isNoContent()); // Ожидаем 204 статус
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
        mockMvc.perform(get("/api/v1/logins/me"))
                .andExpect(status().isUnauthorized());
    }
}