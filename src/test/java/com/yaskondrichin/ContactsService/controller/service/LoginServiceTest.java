package com.yaskondrichin.ContactsService.controller.service;


import com.yaskondrichin.ContactsService.DTO.*;
import com.yaskondrichin.ContactsService.Mapper.LoginMapper;
import com.yaskondrichin.ContactsService.domain.model.Login;
import com.yaskondrichin.ContactsService.domain.model.Role;
import com.yaskondrichin.ContactsService.domain.repo.LoginRepository;
import com.yaskondrichin.ContactsService.exception.ResourceNotFoundException;
import com.yaskondrichin.ContactsService.service.JwtService;
import com.yaskondrichin.ContactsService.service.LoginService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

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
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private LoginService loginService;

    // --- Тесты для findById(Long id) ---

    @Test
    public void findById_WhenUserExists_ShouldReturnLoginResponseDTO() {
        // Given
        Long userId = 1L;
        Login login = new Login();
        login.setId(userId);

        LoginResponseDTO expectedDto = new LoginResponseDTO();

        when(loginRepository.findById(userId)).thenReturn(Optional.of(login));
        when(loginMapper.toResponseDto(login)).thenReturn(expectedDto);

        // When
        LoginResponseDTO result = loginService.findById(userId);

        // Then
        assertNotNull(result);
        assertEquals(expectedDto, result);
        verify(loginRepository, times(1)).findById(userId);
        verify(loginMapper, times(1)).toResponseDto(login);
    }

    @Test
    public void findById_WhenUserDoesNotExist_ShouldThrowRuntimeException() {
        // Given
        Long userId = 99L;
        when(loginRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> loginService.findById(userId));
        assertEquals("Пользователь с ID " + userId + " не найден", exception.getMessage());
        verify(loginMapper, never()).toResponseDto(any());
    }

    // --- Тесты для generateTokensByUserId(Long userId) ---

    @Test
    public void generateTokensByUserId_WhenUserExists_ShouldReturnAuthResponseDTO() {
        // Given
        Long userId = 1L;
        Login login = new Login();
        login.setId(userId);

        TokenResponseDTO mockTokens = mock(TokenResponseDTO.class);
        LoginResponseDTO mockUserDto = new LoginResponseDTO();

        when(loginRepository.findById(userId)).thenReturn(Optional.of(login));
        when(jwtService.generateTokens(login)).thenReturn(mockTokens);
        when(loginMapper.toResponseDto(login)).thenReturn(mockUserDto);

        // When
        AuthResponseDTO result = loginService.generateTokensByUserId(userId);

        // Then
        assertNotNull(result);
        assertEquals(mockUserDto, result.getUser());
        assertEquals(mockTokens, result.getTokens());
        verify(loginRepository, times(1)).findById(userId);
        verify(jwtService, times(1)).generateTokens(login);
        verify(loginMapper, times(1)).toResponseDto(login);
    }

    @Test
    public void generateTokensByUserId_WhenUserDoesNotExist_ShouldThrowRuntimeException() {
        // Given
        Long userId = 99L;
        when(loginRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> loginService.generateTokensByUserId(userId));
        assertEquals("User with ID " + userId + " does not exist", exception.getMessage());
        verify(jwtService, never()).generateTokens(any());
    }

    // --- Тесты для assignRole(AssignRoleDTO dto) ---

    @Test
    public void assignRole_WhenUserExists_ShouldUpdateRole() {
        // Given
        AssignRoleDTO dto = new AssignRoleDTO();
        dto.setUserId(1L);
        dto.setRole(Role.ROLE_ADMIN);

        Login existingLogin = new Login();
        existingLogin.setId(1L);
        existingLogin.setRole(Role.USER);

        when(loginRepository.findById(1L)).thenReturn(Optional.of(existingLogin));
        when(loginRepository.save(any(Login.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        loginService.assignRole(dto);

        // Then
        assertEquals(Role.ROLE_ADMIN, existingLogin.getRole());
        verify(loginRepository, times(1)).findById(1L);
        verify(loginRepository, times(1)).save(existingLogin);
    }

    @Test
    public void assignRole_WhenUserDoesNotExist_ShouldThrowResourceNotFoundException() {
        // Given
        AssignRoleDTO dto = new AssignRoleDTO();
        dto.setUserId(99L);
        dto.setRole(Role.ROLE_ADMIN);

        when(loginRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> loginService.assignRole(dto));
        assertEquals("Пользователь с ID " + dto.getUserId() + " не найден", exception.getMessage());
        verify(loginRepository, never()).save(any());
    }
}