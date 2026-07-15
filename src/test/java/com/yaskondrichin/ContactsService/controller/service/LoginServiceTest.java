package com.yaskondrichin.ContactsService.controller.service;


import com.yaskondrichin.ContactsService.DTO.*;
import com.yaskondrichin.ContactsService.Mapper.LoginMapper;
import com.yaskondrichin.ContactsService.domain.model.Login;
import com.yaskondrichin.ContactsService.domain.enums.Role;
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

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoginServiceTest {

    @Mock
    private LoginRepository loginRepository;

    @Mock
    private LoginMapper loginMapper;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private LoginServiceImpl loginService;

    @Test
    @DisplayName("getLastCreatedUser: должен успешно вернуть последнего созданного пользователя")
    public void getLastCreatedUser_ShouldReturnLastUser() {
        // Given
        Login login = new Login();
        LoginResponseDTO dto = new LoginResponseDTO();
        dto.setLogin("last_user");

        when(loginRepository.findFirstByOrderByIdDesc()).thenReturn(Optional.of(login));
        when(loginMapper.toResponseDto(login)).thenReturn(dto);

        // When
        LoginResponseDTO result = loginService.getLastCreatedUser();

        // Then
        assertNotNull(result);
        assertEquals("last_user", result.getLogin());
        verify(loginRepository, times(1)).findFirstByOrderByIdDesc();
    }

    @Test
    @DisplayName("getLastCreatedUser: должен упасть, если база пуста")
    public void getLastCreatedUser_WhenDbIsEmpty_ShouldThrowException() {
        // Given
        when(loginRepository.findFirstByOrderByIdDesc()).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                loginService.getLastCreatedUser()
        );
        assertEquals("No users found", exception.getMessage());
    }

    @Test
    @DisplayName("getMe: должен вернуть текущего пользователя по JWT")
    public void getMe_ShouldReturnCurrentUser() {
        // Given
        UUID userId = UUID.randomUUID();
        Jwt mockJwt = mock(Jwt.class);
        when(mockJwt.getSubject()).thenReturn(userId.toString());

        Login existingLogin = new Login();
        existingLogin.setId(userId);
        existingLogin.setLogin("test_user");

        when(loginRepository.findById(userId)).thenReturn(Optional.of(existingLogin));

        // When
        Login result = loginService.getMe(mockJwt);

        // Then
        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("test_user", result.getLogin());
        verify(loginRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("findById: должен вернуть DTO пользователя по его ID")
    public void findById_ShouldReturnUserDto() {
        // Given
        UUID userId = UUID.randomUUID();
        Login login = new Login();
        login.setId(userId);

        LoginResponseDTO dto = new LoginResponseDTO();
        dto.setLogin("found_user");

        when(loginRepository.findById(userId)).thenReturn(Optional.of(login));
        when(loginMapper.toResponseDto(login)).thenReturn(dto);

        // When
        LoginResponseDTO result = loginService.findById(userId);

        // Then
        assertNotNull(result);
        assertEquals("found_user", result.getLogin());
    }

    @Test
    @DisplayName("generateTokensByUserId: должен делегировать вызов в JwtService")
    public void generateTokensByUserId_ShouldDelegateToJwtService() {
        // Given
        UUID userId = UUID.randomUUID();
        AuthResponseDTO responseDTO = new AuthResponseDTO();

        when(jwtService.generateTokensByUserId(userId)).thenReturn(responseDTO);

        // When
        AuthResponseDTO result = loginService.generateTokensByUserId(userId);

        // Then
        assertNotNull(result);
        assertEquals(responseDTO, result);
        verify(jwtService, times(1)).generateTokensByUserId(userId);
    }

    @Test
    @DisplayName("assignRole: должен успешно обновить роль пользователя")
    public void assignRole_ShouldUpdateUserRole() {
        // Given
        UUID userId = UUID.randomUUID();

        AssignRoleDTO dto = new AssignRoleDTO();
        dto.setUserId(userId);
        dto.setRole(Role.ROLE_ADMIN); // Передаем корректный Enum

        Login existingLogin = new Login();
        existingLogin.setId(userId);
        existingLogin.setRole(Role.USER);

        when(loginRepository.findById(userId)).thenReturn(Optional.of(existingLogin));

        // When
        loginService.assignRole(dto);

        // Then
        assertEquals(Role.ROLE_ADMIN, existingLogin.getRole());
        verify(loginRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("assignRole: должен выбросить ResourceNotFoundException, если пользователь не найден")
    public void assignRole_WhenUserDoesNotExist_ShouldThrowResourceNotFoundException() {
        // Given
        UUID userId = UUID.randomUUID();

        AssignRoleDTO dto = new AssignRoleDTO();
        dto.setUserId(userId);
        dto.setRole(Role.ROLE_ADMIN);

        when(loginRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                loginService.assignRole(dto)
        );
        assertTrue(exception.getMessage().contains("Пользователь с ID " + dto.getUserId() + " не найден"));
        verify(loginRepository, times(1)).findById(userId);
    }
}