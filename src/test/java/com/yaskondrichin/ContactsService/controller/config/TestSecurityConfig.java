package com.yaskondrichin.ContactsService.controller.config;

import com.yaskondrichin.ContactsService.config.JwtProvider;
import com.yaskondrichin.ContactsService.domain.repo.LoginRepository;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

@TestConfiguration
@EnableWebSecurity
@EnableMethodSecurity
public class TestSecurityConfig {

    @Bean
    @Primary
    public JwtProvider jwtProvider() {
        // Возвращаем Mock, чтобы в тестах контроллеров можно было легко настраивать поведение
        return Mockito.mock(JwtProvider.class);
    }

    @Bean
    @Primary
    public JwtDecoder jwtDecoder() {
        // Заглушка декодера для тестового окружения
        return Mockito.mock(JwtDecoder.class);
    }

    @Bean
    public LoginRepository loginRepository() {
        return Mockito.mock(LoginRepository.class);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Разрешаем все запросы на эндпоинты авторизации без проверки токена
                        .requestMatchers("/auth/**", "/api/v1/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.sendError(jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                        })
                );
        return http.build();
    }
}