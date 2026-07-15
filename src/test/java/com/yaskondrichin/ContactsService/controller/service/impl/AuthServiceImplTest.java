package com.yaskondrichin.ContactsService.controller.service.impl;

import com.yaskondrichin.ContactsService.DTO.RegisterDTO;
import com.yaskondrichin.ContactsService.domain.model.Login;
import com.yaskondrichin.ContactsService.domain.enums.Role;
import com.yaskondrichin.ContactsService.domain.repo.LoginRepository;
import com.yaskondrichin.ContactsService.service.impl.AuthServiceImpl;
import com.yaskondrichin.ContactsService.Utils.PasswordGenerator; // Импортируем новый генератор
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Подключаем Mockito к JUnit 5
class AuthServiceImplTest {

    @Mock
    private LoginRepository loginRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private PasswordGenerator passwordGenerator; // Мокаем генератор паролей

    @InjectMocks
    private AuthServiceImpl authService; // Внедряем заглушки (Mocks) в тестируемый сервис

    @Test
    @DisplayName("register: должен автоматически сгенерировать пароль, захэшировать его и сохранить пользователя в БД")
    void register_ShouldGeneratePasswordEncodeAndSaveLogin() {
        // Given (Исходные данные)
        RegisterDTO dto = new RegisterDTO();
        dto.setUsername("daniil_dev");
        dto.setEmail("daniil@example.com");
        dto.setPhone("+375291112233");

        String mockGeneratedPassword = "generatedPass12"; // Имитируем сгенерированный пароль
        String expectedEncodedPassword = "encoded_password_hash"; // Хэш пароля

        // Настраиваем поведение моков
        when(passwordGenerator.generateRandomPassword()).thenReturn(mockGeneratedPassword);
        when(passwordEncoder.encode(mockGeneratedPassword)).thenReturn(expectedEncodedPassword);

        // Перехватываем то, что пришло на сохранение, и возвращаем этот же объект (имитация сохранения БД)
        when(loginRepository.save(any(Login.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When (Действие)
        Login result = authService.register(dto);

        // Then (Проверки)
        assertNotNull(result);

        // Проверяем, что в transient-поле rawPassword действительно записался наш сгенерированный пароль
        assertEquals(mockGeneratedPassword, result.getRawPassword(),
                "Сгенерированный пароль должен быть записан в transient-поле rawPassword");

        // 1. Проверяем, что вызывался генератор паролей
        verify(passwordGenerator, times(1)).generateRandomPassword();

        // 2. Проверяем, что шифровался именно тот пароль, который вернул генератор
        verify(passwordEncoder, times(1)).encode(mockGeneratedPassword);

        // 3. Перехватываем Login, который ушел в loginRepository.save()
        ArgumentCaptor<Login> loginCaptor = ArgumentCaptor.forClass(Login.class);
        verify(loginRepository, times(1)).save(loginCaptor.capture());
        Login capturedLogin = loginCaptor.getValue();

        // Проверяем корректность сборки сущности Login перед сохранением в базу
        assertNotNull(capturedLogin);
        assertEquals("daniil_dev", capturedLogin.getLogin());
        assertEquals(expectedEncodedPassword, capturedLogin.getPass());
        assertEquals("daniil@example.com", capturedLogin.getEmail());
        assertEquals("+375291112233", capturedLogin.getPhone());
        assertEquals(Role.USER, capturedLogin.getRole(), "По умолчанию новому пользователю должна присваиваться роль USER");
    }
}
