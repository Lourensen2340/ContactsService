package com.yaskondrichin.ContactsService.controller.service.impl;

import com.yaskondrichin.ContactsService.DTO.RegisterDTO;
import com.yaskondrichin.ContactsService.domain.model.Login;
import com.yaskondrichin.ContactsService.domain.enums.Role;
import com.yaskondrichin.ContactsService.domain.repo.LoginRepository;
import com.yaskondrichin.ContactsService.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Подключаем Mockito к JUnit 5
class AuthServiceImplTest {

    @Mock
    private LoginRepository loginRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService; // Внедряем заглушки (Mocks) в тестируемый сервис

    @Test
    void register_ShouldGeneratePasswordEncodeAndSaveLogin() {
        // Given (Предусловия)
        RegisterDTO dto = new RegisterDTO();
        dto.setUsername("daniil_dev");
        dto.setEmail("daniil@example.com");
        dto.setPhone("+375291112233");

        String expectedEncodedPassword = "$2a$10$encodedPasswordHashHere";

        // Так как пароль внутри сервиса генерируется случайным образом,
        // настраиваем кодировщик на перехват любой входящей строки
        when(passwordEncoder.encode(anyString())).thenReturn(expectedEncodedPassword);

        // Возвращаем тот же объект Login, который пришел на сохранение, имитируя поведение БД
        when(loginRepository.save(any(Login.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When (Действие)
        Login result = authService.register(dto);

        // Then (Проверки)
        assertNotNull(result);
        assertNotNull(result.getRawPassword(), "Сгенерированный пароль должен быть записан в transient-поле rawPassword");
        assertEquals(12, result.getRawPassword().length(), "Длина автоматически сгенерированного пароля должна быть ровно 12 символов");

        // 1. Проверяем, что шифровался именно тот пароль, который был сгенерирован автоматически
        verify(passwordEncoder, times(1)).encode(result.getRawPassword());

        // 2. Перехватываем Login, который ушел в loginRepository.save()
        ArgumentCaptor<Login> loginCaptor = ArgumentCaptor.forClass(Login.class);
        verify(loginRepository, times(1)).save(loginCaptor.capture());
        Login capturedLogin = loginCaptor.getValue();

        // Проверяем корректность сборки сущности Login
        assertNotNull(capturedLogin);
        assertEquals("daniil_dev", capturedLogin.getLogin());
        assertEquals(expectedEncodedPassword, capturedLogin.getPass());
        assertEquals("daniil@example.com", capturedLogin.getEmail());
        assertEquals("+375291112233", capturedLogin.getPhone());
        assertEquals(Role.USER, capturedLogin.getRole()); // Проверяем дефолтную роль пользователя
    }
}
