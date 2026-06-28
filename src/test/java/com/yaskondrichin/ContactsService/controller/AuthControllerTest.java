package com.yaskondrichin.ContactsService.controller;

import com.yaskondrichin.ContactsService.DTO.*;
import com.yaskondrichin.ContactsService.Mapper.LoginMapper;
import com.yaskondrichin.ContactsService.config.JwtProvider;
import com.yaskondrichin.ContactsService.controller.config.TestSecurityConfig;
import com.yaskondrichin.ContactsService.domain.controller.AuthController;
import com.yaskondrichin.ContactsService.domain.model.Login;
import com.yaskondrichin.ContactsService.domain.repo.LoginRepository;
import com.yaskondrichin.ContactsService.service.AuthService;
import com.yaskondrichin.ContactsService.service.JwtService;
import com.yaskondrichin.ContactsService.service.LoginService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
// Импортируем провайдер и конфигурацию безопасности, чтобы избежать 401 ошибок
@Import({JwtProvider.class, TestSecurityConfig.class})
public class AuthControllerTest {
    @MockitoBean
    private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    @MockitoBean
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private LoginRepository loginRepository;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @MockitoBean
    private LoginService loginService;

    @MockitoBean
    private LoginMapper loginMapper;

    @Test
    public void login_WithValidCredentials_ShouldReturnTokens() throws Exception {
        TokenResponseDTO mockTokens = new TokenResponseDTO("access-token", "refresh-token");
        Login mockUser = new Login();

        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(loginRepository.findByLogin("test_user")).thenReturn(java.util.Optional.of(mockUser));
        when(jwtService.generateTokens(mockUser)).thenReturn(mockTokens);

        String loginJson = """
            {
                "login": "test_user",
                "password": "password123"
            }
            """;

        // Убрали префикс /api/v1, теперь путь совпадает с контроллером
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"));
    }

    @Test
    public void registerUser_ShouldReturnTokensDto() throws Exception {
        Login mockUser = new Login();
        TokenResponseDTO mockTokens = new TokenResponseDTO("reg-access-token", "reg-refresh-token");

        when(authService.register(any(RegisterDTO.class))).thenReturn(mockUser);
        // Использована правильная переменная jwtServiсe (с вашей кириллической 'с')
        when(jwtService.generateTokens(any(Login.class))).thenReturn(mockTokens);

        String registerJson = """
        {
            "login": "new_user",
            "password": "password123",
            "email": "new_user@mail.com"
        }
        """;


        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("reg-access-token"))
                .andExpect(jsonPath("$.refreshToken").value("reg-refresh-token"));
    }
    @Test
    public void exchangeIdForTokens_ShouldReturnAuthResponse() throws Exception {
        AuthResponseDTO mockAuthResponse = new AuthResponseDTO();

        when(loginService.generateTokensByUserId(1L)).thenReturn(mockAuthResponse);

        mockMvc.perform(post("/api/v1/auth/exchange")
                        .param("userId", "1")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .with(csrf()))
                .andExpect(status().isOk());
    }
}