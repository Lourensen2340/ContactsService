package com.yaskondrichin.ContactsService.controller;



import com.yaskondrichin.ContactsService.DTO.LoginResponseDTO;
import com.yaskondrichin.ContactsService.Mapper.LoginMapper;
import com.yaskondrichin.ContactsService.config.JwtProvider;
import com.yaskondrichin.ContactsService.controller.config.TestSecurityConfig;
import com.yaskondrichin.ContactsService.domain.controller.LoginController;
import com.yaskondrichin.ContactsService.domain.model.Login;
import com.yaskondrichin.ContactsService.domain.repo.LoginRepository;
import com.yaskondrichin.ContactsService.service.LoginService;
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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(LoginController.class)
@Import({JwtProvider.class, TestSecurityConfig.class})
public class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @org.springframework.test.context.bean.override.mockito.MockitoBean
    private LoginService loginService;

    @MockitoBean
    private LoginMapper loginMapper;

    @org.springframework.test.context.bean.override.mockito.MockitoBean
    private LoginRepository loginRepository;

    @MockitoBean
    private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    @MockitoBean
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
    @MockitoBean
    private JwtDecoder jwtDecoder; // Добавлено для корректной работы JWT-фильтров в WebMvcTest контексте

    @Test
    public void getMe_WhenAuthenticated_ShouldReturnOkAndMappedUser() throws Exception {
        Login mockLogin = Mockito.mock(Login.class);
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
        mockMvc.perform(get("/api/v1/logins/me"))
                .andExpect(status().isUnauthorized());
    }
}