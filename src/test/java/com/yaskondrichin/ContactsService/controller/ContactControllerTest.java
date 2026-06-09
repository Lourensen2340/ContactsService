package com.yaskondrichin.ContactsService.controller;

import com.yaskondrichin.ContactsService.service.ContactService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest // Загружает полный контекст приложения, где корректно работают все прокси
@AutoConfigureMockMvc // Автоматически настраивает MockMvc со всей цепочкой безопасности
public class ContactControllerTest {

    @MockitoBean
    private com.yaskondrichin.ContactsService.config.JwtProvider jwtProvider;

    @MockitoBean
    private com.yaskondrichin.ContactsService.domain.repo.LoginRepository loginRepository;

    @MockitoBean
    private ContactService contactService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getAll_WhenUser_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/contacts")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")))) // Передаем обычного пользователя
                .andExpect(status().isForbidden()); // Теперь железно вернется 403 Forbidden
    }
}