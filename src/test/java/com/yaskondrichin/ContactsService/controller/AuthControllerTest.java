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
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
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

    // Эндпоинт login закомментирован в AuthController, тест тоже комментируем
    /*
    @Test
    public void login_WithValidCredentials_ShouldReturnTokens() throws Exception {
        ...
    }
    */

    @Test
    public void registerUser_ShouldReturnTokensDto() throws Exception {
        Login mockUser = new Login();
        TokenResponseDTO mockTokens = new TokenResponseDTO("reg-access-token", "reg-refresh-token");
        LoginResponseDTO mockUserDto = new LoginResponseDTO();

        when(authService.register(any(RegisterDTO.class))).thenReturn(mockUser);
        when(jwtService.generateTokens(any(Login.class))).thenReturn(mockTokens);
        when(loginMapper.toResponseDto(any(Login.class))).thenReturn(mockUserDto);

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
                // Токены теперь находятся внутри объекта tokens в AuthResponseDTO
                .andExpect(jsonPath("$.tokens.accessToken").value("reg-access-token"))
                .andExpect(jsonPath("$.tokens.refreshToken").value("reg-refresh-token"));
    }

    @Test
    public void exchangeIdForTokens_ShouldReturnAuthResponse() throws Exception {
        AuthResponseDTO mockAuthResponse = new AuthResponseDTO();
        String validUuid = UUID.randomUUID().toString();

        when(loginService.generateTokensByUserId(any(UUID.class))).thenReturn(mockAuthResponse);

        mockMvc.perform(post("/api/v1/auth/exchange")
                        .param("userId", validUuid)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .with(csrf()))
                .andExpect(status().isOk());
    }
}