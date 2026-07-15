package com.yaskondrichin.ContactsService.controller.service;

import com.yaskondrichin.ContactsService.DTO.AuthResponseDTO;
import com.yaskondrichin.ContactsService.DTO.LoginResponseDTO;
import com.yaskondrichin.ContactsService.DTO.TokenResponseDTO;
import com.yaskondrichin.ContactsService.Mapper.LoginMapper;
import com.yaskondrichin.ContactsService.domain.enums.Role;
import com.yaskondrichin.ContactsService.domain.model.Login;
import com.yaskondrichin.ContactsService.domain.repo.LoginRepository;
import com.yaskondrichin.ContactsService.service.impl.JwtServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.*;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @Mock
    private JwtEncoder jwtEncoder;

    @Mock
    private JwtDecoder jwtDecoder;

    @Mock
    private LoginRepository loginRepository;

    @Mock
    private LoginMapper loginMapper;

    @InjectMocks
    private JwtServiceImpl jwtService;

    @Test
    @DisplayName("generateTokens: должен вернуть Access и Refresh токены с правильными клеймами")
    void generateTokens_ShouldReturnAccessAndRefreshTokensWithCorrectClaims() {
        // Given
        UUID mockId = UUID.randomUUID();
        Login login = new Login();
        login.setId(mockId);
        login.setLogin("test_user");
        login.setRole(Role.USER);

        Jwt mockJwt = mock(Jwt.class);
        when(mockJwt.getTokenValue()).thenReturn("mock-token-value");
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(mockJwt);

        // When
        TokenResponseDTO result = jwtService.generateTokens(login);

        // Then
        assertNotNull(result);
        assertEquals("mock-token-value", result.getAccessToken());
        assertEquals("mock-token-value", result.getRefreshToken());

        ArgumentCaptor<JwtEncoderParameters> parametersCaptor = ArgumentCaptor.forClass(JwtEncoderParameters.class);
        verify(jwtEncoder, times(2)).encode(parametersCaptor.capture());

        var allCapturedParameters = parametersCaptor.getAllValues();

        // 1. Извлекаем и проверяем Access Token Claims
        JwtClaimsSet accessClaims = allCapturedParameters.get(0).getClaims();
        assertNotNull(accessClaims.getIssuer(), "Issuer не должен быть null");
        // Приводим к String через toString(), чтобы избежать ошибки сравнения URL со String
        assertEquals("http://localhost:8080", accessClaims.getIssuer().toString());
        assertEquals(mockId.toString(), accessClaims.getSubject());
        assertEquals("USER", accessClaims.getClaim("roles"));

        // 2. Извлекаем и проверяем Refresh Token Claims
        JwtClaimsSet refreshClaims = allCapturedParameters.get(1).getClaims();
        assertNotNull(refreshClaims.getIssuer(), "Issuer не должен быть null");
        // Аналогично приводим к String
        assertEquals("http://localhost:8080", refreshClaims.getIssuer().toString());
        assertEquals(mockId.toString(), refreshClaims.getSubject());
        assertNull(refreshClaims.getClaim("roles"), "Refresh токен не должен содержать роли");
    }

    @Test
    @DisplayName("generateTokens: должен назначить роль USER по умолчанию, если у пользователя роль null")
    void generateTokens_ShouldDefaultToUserRole_WhenRoleIsNull() {
        // Given
        UUID mockId = UUID.randomUUID();
        Login login = new Login();
        login.setId(mockId);
        login.setLogin("guest_user");
        login.setRole(null);

        Jwt mockJwt = mock(Jwt.class);
        when(mockJwt.getTokenValue()).thenReturn("any-token");
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(mockJwt);

        // When
        jwtService.generateTokens(login);

        // Then
        ArgumentCaptor<JwtEncoderParameters> parametersCaptor = ArgumentCaptor.forClass(JwtEncoderParameters.class);
        verify(jwtEncoder, times(2)).encode(parametersCaptor.capture());

        JwtClaimsSet accessClaims = parametersCaptor.getAllValues().get(0).getClaims();
        assertEquals("USER", accessClaims.getClaim("roles"));
    }

    @Test
    @DisplayName("getUserIdFromToken: должен успешно вернуть UUID из валидного токена")
    void getUserIdFromToken_ValidToken_ShouldReturnUUID() {
        // Given
        String token = "valid.jwt.token";
        UUID expectedUuid = UUID.randomUUID();
        Jwt mockJwt = mock(Jwt.class);

        when(mockJwt.getSubject()).thenReturn(expectedUuid.toString());
        when(jwtDecoder.decode(token)).thenReturn(mockJwt);

        // When
        UUID actualUuid = jwtService.getUserIdFromToken(token, true);

        // Then
        assertNotNull(actualUuid);
        assertEquals(expectedUuid, actualUuid);
        verify(jwtDecoder, times(1)).decode(token);
    }

    @Test
    @DisplayName("getUserIdFromToken: должен вернуть null, если токен невалидный или возникла ошибка")
    void getUserIdFromToken_InvalidToken_ShouldReturnNull() {
        // Given
        String token = "invalid.token";
        when(jwtDecoder.decode(token)).thenThrow(new JwtException("Invalid token signature"));

        // When
        UUID actualUuid = jwtService.getUserIdFromToken(token, true);

        // Then
        assertNull(actualUuid);
    }

    @Test
    @DisplayName("generateTokensByUserId: должен успешно сгенерировать полный AuthResponseDTO для существующего пользователя")
    void generateTokensByUserId_ShouldReturnAuthResponse_WhenUserExists() {
        // Given
        UUID userId = UUID.randomUUID();
        Login user = new Login();
        user.setId(userId);
        user.setLogin("user_login");
        user.setRole(Role.USER);

        LoginResponseDTO userDto = new LoginResponseDTO();
        userDto.setLogin("user_login");

        Jwt mockJwt = mock(Jwt.class);
        when(mockJwt.getTokenValue()).thenReturn("generated-token");

        when(loginRepository.findById(userId)).thenReturn(Optional.of(user));
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(mockJwt);
        when(loginMapper.toResponseDto(user)).thenReturn(userDto);

        // When
        AuthResponseDTO result = jwtService.generateTokensByUserId(userId);

        // Then
        assertNotNull(result);
        assertEquals(userDto, result.getUser());
        assertEquals("generated-token", result.getTokens().getAccessToken());
        verify(loginRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("generateTokensByUserId: должен выбросить исключение, если пользователь с ID не найден")
    void generateTokensByUserId_UserNotFound_ShouldThrowException() {
        // Given
        UUID userId = UUID.randomUUID();
        when(loginRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                jwtService.generateTokensByUserId(userId)
        );

        assertTrue(exception.getMessage().contains("User with ID " + userId + " does not exist"));
        verifyNoInteractions(jwtEncoder);
    }
}