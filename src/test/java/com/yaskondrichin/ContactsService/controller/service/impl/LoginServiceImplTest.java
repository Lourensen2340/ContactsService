package com.yaskondrichin.ContactsService.controller.service.impl;

import com.yaskondrichin.ContactsService.DTO.*;
import com.yaskondrichin.ContactsService.Mapper.LoginMapper;
import com.yaskondrichin.ContactsService.domain.enums.Role;
import com.yaskondrichin.ContactsService.domain.model.Login;
import com.yaskondrichin.ContactsService.domain.repo.LoginRepository;
import com.yaskondrichin.ContactsService.exception.ResourceNotFoundException;
import com.yaskondrichin.ContactsService.service.JwtService;
import com.yaskondrichin.ContactsService.service.impl.LoginServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginServiceImplTest {

    @Mock
    private LoginRepository loginRepository;

    @Mock
    private LoginMapper loginMapper;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private LoginServiceImpl loginService;
    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    @DisplayName("getLastCreatedUser: должен вернуть последнего созданного пользователя, если база не пуста")
    void getLastCreatedUser_ShouldReturnLastUser() {
        // Given
        Login mockLogin = new Login();
        LoginResponseDTO mockResponseDto = new LoginResponseDTO();
        mockResponseDto.setLogin("last_user");

        when(loginRepository.findFirstByOrderByIdDesc()).thenReturn(Optional.of(mockLogin));
        when(loginMapper.toResponseDto(mockLogin)).thenReturn(mockResponseDto);

        // When
        LoginResponseDTO result = loginService.getLastCreatedUser();

        // Then
        assertNotNull(result);
        assertEquals("last_user", result.getLogin());
        verify(loginRepository, times(1)).findFirstByOrderByIdDesc();
        verify(loginMapper, times(1)).toResponseDto(mockLogin);
    }

    @Test
    @DisplayName("getLastCreatedUser: должен выбросить RuntimeException, если пользователей в БД нет")
    void getLastCreatedUser_EmptyDb_ShouldThrowException() {
        // Given
        when(loginRepository.findFirstByOrderByIdDesc()).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                loginService.getLastCreatedUser()
        );

        assertEquals("No users found", exception.getMessage());
        verify(loginRepository, times(1)).findFirstByOrderByIdDesc();
        verifyNoInteractions(loginMapper);
    }

    @Test
    @DisplayName("getMe: должен успешно вернуть текущего пользователя из Security Context по ID из JWT")
    void getMe_ShouldReturnCurrentUser() {
        // Given
        UUID userId = UUID.randomUUID();
        Jwt mockJwt = mock(Jwt.class);
        when(mockJwt.getSubject()).thenReturn(userId.toString());

        Login mockLogin = new Login();
        mockLogin.setId(userId);
        mockLogin.setLogin("current_user");

        when(loginRepository.findById(userId)).thenReturn(Optional.of(mockLogin));

        // When
        Login result = loginService.getMe(mockJwt);

        // Then
        assertNotNull(result);
        assertEquals("current_user", result.getLogin());
        assertEquals(userId, result.getId());
        verify(loginRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("getMe: должен выбросить RuntimeException, если ID пользователя из JWT отсутствует в базе")
    void getMe_UserNotFound_ShouldThrowException() {
        // Given
        UUID userId = UUID.randomUUID();
        Jwt mockJwt = mock(Jwt.class);
        when(mockJwt.getSubject()).thenReturn(userId.toString());

        when(loginRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                loginService.getMe(mockJwt)
        );

        assertEquals("Пользователь не найден", exception.getMessage());
        verify(loginRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("findById: должен успешно вернуть DTO пользователя по его ID")
    void findById_ShouldReturnUserDto() {
        // Given
        UUID id = UUID.randomUUID();
        Login mockLogin = new Login();
        LoginResponseDTO mockResponseDto = new LoginResponseDTO();
        mockResponseDto.setLogin("test_user_by_id");

        when(loginRepository.findById(id)).thenReturn(Optional.of(mockLogin));
        when(loginMapper.toResponseDto(mockLogin)).thenReturn(mockResponseDto);

        // When
        LoginResponseDTO result = loginService.findById(id);

        // Then
        assertNotNull(result);
        assertEquals("test_user_by_id", result.getLogin());
        verify(loginRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("findById: должен выбросить RuntimeException, если пользователь с таким ID не существует")
    void findById_UserNotFound_ShouldThrowException() {
        // Given
        UUID id = UUID.randomUUID();
        when(loginRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                loginService.findById(id)
        );

        assertTrue(exception.getMessage().contains("Пользователь с ID " + id + " не найден"));
        verify(loginRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("generateTokensByUserId: должен перенаправить вызов генерации токенов в JwtService")
    void generateTokensByUserId_ShouldDelegateToJwtService() {
        // Given
        UUID userId = UUID.randomUUID();
        AuthResponseDTO mockAuthResponse = new AuthResponseDTO();

        when(jwtService.generateTokensByUserId(userId)).thenReturn(mockAuthResponse);

        // When
        AuthResponseDTO result = loginService.generateTokensByUserId(userId);

        // Then
        assertNotNull(result);
        assertEquals(mockAuthResponse, result);
        verify(jwtService, times(1)).generateTokensByUserId(userId);
    }

    @Test
    @DisplayName("assignRole: должен успешно обновить роль у существующего пользователя")
    void assignRole_ShouldUpdateUserRole() {
        // Given
        UUID userId = UUID.randomUUID();
        AssignRoleDTO assignRoleDTO = new AssignRoleDTO();
        assignRoleDTO.setUserId(userId);

        // ИСПРАВЛЕНО: Передаем реальный Enum Role.ADMIN вместо строки "ADMIN"
        assignRoleDTO.setRole(Role.ROLE_ADMIN);

        Login mockLogin = new Login();
        mockLogin.setId(userId);

        when(loginRepository.findById(userId)).thenReturn(Optional.of(mockLogin));

        // When
        loginService.assignRole(assignRoleDTO);

        // Then
        verify(loginRepository, times(1)).findById(userId);

        // Проверяем, что сущности пользователя была присвоена ожидаемая роль
        assertEquals(Role.ROLE_ADMIN, mockLogin.getRole());
    }

    @Test
    @DisplayName("assignRole: должен выбросить ResourceNotFoundException, если пользователь для смены роли не найден")
    void assignRole_UserNotFound_ShouldThrowResourceNotFoundException() {
        // Given
        UUID userId = UUID.randomUUID();
        AssignRoleDTO assignRoleDTO = new AssignRoleDTO();
        assignRoleDTO.setUserId(userId);

        when(loginRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                loginService.assignRole(assignRoleDTO)
        );

        assertTrue(exception.getMessage().contains("Пользователь с ID " + userId + " не найден"));
        verify(loginRepository, times(1)).findById(userId);
    }
}
