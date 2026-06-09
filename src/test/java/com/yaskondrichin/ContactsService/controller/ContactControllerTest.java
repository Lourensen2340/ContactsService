package com.yaskondrichin.ContactsService.controller;

import com.yaskondrichin.ContactsService.controller.config.TestSecurityConfig;
import com.yaskondrichin.ContactsService.domain.controller.ContactController;
import com.yaskondrichin.ContactsService.service.ContactService;
import com.yaskondrichin.ContactsService.config.SecurityConfig; // <-- ИМПОРТИРУЙТЕ ВАШ ОСНОВНОЙ SecurityConfig

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan; // <-- ДОБАВИТЬ
import org.springframework.context.annotation.FilterType;    // <-- ДОБАВИТЬ
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        value = ContactController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = SecurityConfig.class // <-- Исключаем основной конфиг безопасности из этого теста
        )
)
@Import({
        TestSecurityConfig.class,
        AopAutoConfiguration.class, // Активирует АОП-прокси для работы @PreAuthorize
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration.class
})
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
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                        .andExpect(status().isForbidden());
    }
}