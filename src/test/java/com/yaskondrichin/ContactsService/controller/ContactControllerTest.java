package com.yaskondrichin.ContactsService.controller;

import com.yaskondrichin.ContactsService.controller.config.TestSecurityConfig;
import com.yaskondrichin.ContactsService.domain.controller.ContactController;
import com.yaskondrichin.ContactsService.service.ContactService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration; // ВАЖНО: АОП нужен для @PreAuthorize
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity; // ВАЖНО
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ContactController.class)
@Import({
        TestSecurityConfig.class,
        AopAutoConfiguration.class, // Активирует АОП-прокси для контроллера
        ContactControllerTest.MethodSecurityTestConfig.class // Активирует проверку @PreAuthorize
})
public class ContactControllerTest {
    @MockitoBean
    private com.yaskondrichin.ContactsService.config.JwtProvider jwtProvider;
    @MockitoBean
    private com.yaskondrichin.ContactsService.domain.repo.LoginRepository loginRepository;
    @MockitoBean
    private ContactService contactService;

    // Локальная конфигурация для включения защиты методов
    @TestConfiguration
    @EnableMethodSecurity
    static class MethodSecurityTestConfig {}
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        // КРИТИЧЕСКИ ВАЖНО: вручную активируем фильтры безопасности для MockMvc
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    public void getAll_WhenUser_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/contacts")
                        // .with(jwt()) создает правильный объект аутентификации
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isForbidden()); // Теперь метод вернет 403, а не 400
    }
}