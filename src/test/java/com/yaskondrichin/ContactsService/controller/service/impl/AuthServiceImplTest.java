package com.yaskondrichin.ContactsService.controller.service.impl;

import com.yaskondrichin.ContactsService.DTO.RegisterDTO;
import com.yaskondrichin.ContactsService.domain.model.Login;
import com.yaskondrichin.ContactsService.domain.model.Role;
import com.yaskondrichin.ContactsService.domain.model.User;
import com.yaskondrichin.ContactsService.domain.repo.LoginRepository;
import com.yaskondrichin.ContactsService.domain.repo.UserRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Подключаем Mockito к JUnit 5
class AuthServiceImplTest {

    @Mock
    private LoginRepository loginRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService; // Внедряем заглушки (Mocks) в тестируемый сервис

    @Test
    void register_ShouldEncodePasswordAndSaveLoginAndUser() {
        // Given (Предусловия)
        RegisterDTO dto = new RegisterDTO();
        dto.setUsername("daniil_dev");
        dto.setPassword("superSecret123");
        dto.setEmail("daniil@example.com");
        dto.setPhone("+375291112233");

        String expectedEncodedPassword = "$2a$10$encodedPasswordHashHere";

        // Настраиваем поведение заглушек
        when(passwordEncoder.encode("superSecret123")).thenReturn(expectedEncodedPassword);

        // Возвращаем тот же объект Login, который пришел на сохранение, имитируя поведение БД
        when(loginRepository.save(any(Login.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When (Действие)
        authService.register(dto);

        // Then (Проверки)

        // 1. Проверяем, что пароль действительно шифровался ровно 1 раз
        verify(passwordEncoder, times(1)).encode("superSecret123");

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
        assertEquals(Role.USER, capturedLogin.getRole()); // Проверяем дефолтную роль

        // 3. Перехватываем User, который ушел в userRepository.save()
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(userCaptor.capture());
        User capturedUser = userCaptor.getValue();

        // Проверяем корректность сборки сущности User
        assertNotNull(capturedUser);
        assertEquals("daniil_dev", capturedUser.getUsername());
        assertEquals("daniil@example.com", capturedUser.getEmail());
        assertEquals(expectedEncodedPassword, capturedUser.getPassword()); // Пароль берется из сохраненного Login
    }
}
