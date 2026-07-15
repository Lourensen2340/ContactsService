package com.yaskondrichin.ContactsService.controller.service.impl;

import com.yaskondrichin.ContactsService.DTO.AuthResponseDTO;
import com.yaskondrichin.ContactsService.DTO.LoginResponseDTO;
import com.yaskondrichin.ContactsService.DTO.TokenResponseDTO;
import com.yaskondrichin.ContactsService.Mapper.LoginMapper;
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
class JwtServiceImplTest {

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

    // Вспомогательное перечисление для роли, если у вас в Login используется enum Role
    // Если используется класс или String, адаптируйте под вашу кодовую базу
    private enum TestRole {
        USER, ADMIN
    }

    @Test
    @DisplayName("Генерация токенов: должен успешно закодировать Access и Refresh токен с корректными Claims")
    void generateTokens_ShouldCreateAccessAndRefreshTokens() {
        // Given
        UUID userId = UUID.randomUUID();
        Login mockUser = new Login();
        mockUser.setId(userId);
        mockUser.setLogin("test_user");

        // Эмулируем наличие роли USER (предусматриваем сейф-гард на null из вашего кода)
        // Если у вас в Login используется обычный enum, раскомментируйте или оставьте null
        // mockUser.setRole(Login.Role.USER);

        Jwt mockAccessJwt = mock(Jwt.class);
        when(mockAccessJwt.getTokenValue()).thenReturn("mocked-access-token");

        Jwt mockRefreshJwt = mock(Jwt.class);
        when(mockRefreshJwt.getTokenValue()).thenReturn("mocked-refresh-token");

        // При первом вызове encode() возвращаем access, при втором - refresh
        when(jwtEncoder.encode(any(JwtEncoderParameters.class)))
                .thenReturn(mockAccessJwt)
                .thenReturn(mockRefreshJwt);

        // When
        TokenResponseDTO tokens = jwtService.generateTokens(mockUser);

        // Then
        assertNotNull(tokens);
        assertEquals("mocked-access-token", tokens.getAccessToken());
        assertEquals("mocked-refresh-token", tokens.getRefreshToken());

        // Проверяем claims, которые уходят в энкодер
        ArgumentCaptor<JwtEncoderParameters> parametersCaptor = ArgumentCaptor.forClass(JwtEncoderParameters.class);
        verify(jwtEncoder, times(2)).encode(parametersCaptor.capture());

        JwtEncoderParameters accessParams = parametersCaptor.getAllValues().get(0);
        JwtClaimsSet accessClaims = accessParams.getClaims();

        assertEquals(userId.toString(), accessClaims.getSubject());
        // Проверка issuer из корректного объекта accessClaims
        assertNotNull(accessClaims.getIssuer(), "Issuer не должен быть null");
        assertEquals("http://localhost:8080", accessClaims.getIssuer().toString());

        JwtEncoderParameters refreshParams = parametersCaptor.getAllValues().get(1);
        JwtClaimsSet refreshClaims = refreshParams.getClaims();
        assertEquals(userId.toString(), refreshClaims.getSubject());
        // Также можно проверить и на refresh токен
        assertNotNull(refreshClaims.getIssuer(), "Issuer не должен быть null");
        assertEquals("http://localhost:8080", refreshClaims.getIssuer().toString());
    }

    @Test
    @DisplayName("Извлечение ID: должен успешно вернуть UUID из валидного токена")
    void getUserIdFromToken_WithValidToken_ShouldReturnUUID() {
        // Given
        String token = "valid.token.string";
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
    @DisplayName("Извлечение ID: должен вернуть null, если токен невалидный или произошла ошибка")
    void getUserIdFromToken_WithInvalidToken_ShouldReturnNull() {
        // Given
        String token = "invalid-token";
        when(jwtDecoder.decode(token)).thenThrow(new BadJwtException("Invalid signature"));

        // When
        UUID actualUuid = jwtService.getUserIdFromToken(token, true);

        // Then
        assertNull(actualUuid);
    }

    @Test
    @DisplayName("Обмен ID на токены: должен вернуть AuthResponseDTO для существующего пользователя")
    void generateTokensByUserId_UserExists_ShouldReturnAuthResponse() {
        // Given
        UUID userId = UUID.randomUUID();
        Login mockUser = new Login();
        mockUser.setId(userId);
        mockUser.setLogin("existing_user");

        LoginResponseDTO mockUserDto = new LoginResponseDTO();
        mockUserDto.setLogin("existing_user");

        Jwt mockAccessJwt = mock(Jwt.class);
        when(mockAccessJwt.getTokenValue()).thenReturn("acc-token");
        Jwt mockRefreshJwt = mock(Jwt.class);
        when(mockRefreshJwt.getTokenValue()).thenReturn("ref-token");

        when(loginRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(jwtEncoder.encode(any(JwtEncoderParameters.class)))
                .thenReturn(mockAccessJwt)
                .thenReturn(mockRefreshJwt);
        when(loginMapper.toResponseDto(mockUser)).thenReturn(mockUserDto);

        // When
        AuthResponseDTO response = jwtService.generateTokensByUserId(userId);

        // Then
        assertNotNull(response);
        assertEquals(mockUserDto, response.getUser());
        assertNotNull(response.getTokens());
        assertEquals("acc-token", response.getTokens().getAccessToken());
        assertEquals("ref-token", response.getTokens().getRefreshToken());

        verify(loginRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("Обмен ID на токены: должен выбросить RuntimeException, если пользователь не найден в БД")
    void generateTokensByUserId_UserDoesNotExist_ShouldThrowException() {
        // Given
        UUID missingUserId = UUID.randomUUID();
        when(loginRepository.findById(missingUserId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                jwtService.generateTokensByUserId(missingUserId)
        );

        assertTrue(exception.getMessage().contains("User with ID " + missingUserId + " does not exist"));
        verify(loginRepository, times(1)).findById(missingUserId);
        verifyNoInteractions(jwtEncoder);
        verifyNoInteractions(loginMapper);
    }
}
