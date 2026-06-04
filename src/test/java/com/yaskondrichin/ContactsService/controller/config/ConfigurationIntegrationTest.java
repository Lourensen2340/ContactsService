package com.yaskondrichin.ContactsService.controller.config;

import com.yaskondrichin.ContactsService.config.AuthenticatedUserIdResolver;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ConfigurationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtEncoder jwtEncoder;

    @Autowired
    private RequestMappingHandlerAdapter adapter;
    @Transactional
    @Test
    public void securityBeans_ShouldBeCorrectlyInitialized() {
        // Проверяем, что бины конфигурации безопасности создаются успешно
        assertNotNull(passwordEncoder);
        assertNotNull(jwtEncoder);

        // Проверяем работу кодировщика паролей из SecurityConfig
        String rawPassword = "password123";
        String encodedPassword = passwordEncoder.encode(rawPassword);
        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword));
    }
    @Transactional
    @Test
    public void webConfig_ShouldRegisterCustomArgumentResolvers() {
        // Проверяем, добавились ли резолверы из WebConfig в общую цепочку Spring MVC
        List<HandlerMethodArgumentResolver> resolvers = adapter.getArgumentResolvers();

        assertNotNull(resolvers);

        boolean hasAuthenticatedUserIdResolver = resolvers.stream()
                .anyMatch(r -> r instanceof AuthenticatedUserIdResolver);

        assertTrue(hasAuthenticatedUserIdResolver,
                "AuthenticatedUserIdResolver должен быть зарегистрирован в WebMvcConfigurer");
    }
}
