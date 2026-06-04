package com.yaskondrichin.ContactsService.controller;



import com.yaskondrichin.ContactsService.DTO.AuthResponseDTO;
import com.yaskondrichin.ContactsService.DTO.LoginRequestDTO;
import com.yaskondrichin.ContactsService.DTO.LoginResponseDTO;
import com.yaskondrichin.ContactsService.controller.config.TestSecurityConfig;
import com.yaskondrichin.ContactsService.domain.controller.LoginController;
import com.yaskondrichin.ContactsService.domain.model.Login;
import com.yaskondrichin.ContactsService.service.LoginService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoginController.class)
@Import(TestSecurityConfig.class)
public class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LoginService loginService;

    @MockitoBean
    private JwtDecoder jwtDecoder; // Отключает запросы к реальному Keycloak

    @MockitoBean
    private com.yaskondrichin.ContactsService.config.JwtProvider jwtProvider;

    @MockitoBean
    private com.yaskondrichin.ContactsService.domain.repo.LoginRepository loginRepository;

    @MockitoBean
    private com.yaskondrichin.ContactsService.Mapper.LoginMapper loginMapper;

    @Test
    public void getMe_WhenAuthenticated_ShouldReturnOkAndMappedUser() throws Exception {
        Login mockLogin = new Login();
        LoginResponseDTO responseDto = new LoginResponseDTO();
        responseDto.setId(10L);
        responseDto.setLogin("ownerUser");

        // Мокаем получение текущего пользователя из токена и его маппинг в DTO
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
    public void register_WithValidData_ShouldReturnOkAndAuthTokens() throws Exception {
        AuthResponseDTO authResponse = new AuthResponseDTO();
        // Можно наполнить DTO токенами, если это проверяется в jsonPath:
        // authResponse.setAccessToken("mock-access-token");

        Mockito.when(loginService.register(Mockito.any(LoginRequestDTO.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/v1/logins/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"login\":\"Billy\",\"pass\":\"securePassword123\",\"email\":\"Billy@example.com\",\"phone\":\"+375915625467\"}"))
                .andExpect(status().isOk());
    }

    @Test
    public void debug_ShouldReturnCorrectStringWithSubject() throws Exception {
        mockMvc.perform(get("/api/v1/logins/debug")
                        .with(jwt().jwt(jwt -> jwt.subject("custom_sub"))))
                .andExpect(status().isOk())
                // Проверяем точное соответствие возвращаемой строке из контроллера
                .andExpect(content().string("Your ID from token (sub): custom_sub"));
    }
}