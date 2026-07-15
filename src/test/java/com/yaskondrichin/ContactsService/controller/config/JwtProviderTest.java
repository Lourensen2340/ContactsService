package com.yaskondrichin.ContactsService.controller.config;

import com.yaskondrichin.ContactsService.config.JwtProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class JwtProviderTest {

    @Mock
    private JwtDecoder jwtDecoder;

    @InjectMocks
    private JwtProvider jwtProvider;

    @Test
    @DisplayName("Должен успешно вернуть UUID, если токен валидный и содержит UUID в subject")
    public void getUserIdFromToken_ValidToken_ShouldReturnUUID() {
        // Given
        String token = "valid-token";
        UUID expectedUuid = UUID.randomUUID();

        Jwt mockJwt = Mockito.mock(Jwt.class);
        Mockito.when(mockJwt.getSubject()).thenReturn(expectedUuid.toString());
        Mockito.when(jwtDecoder.decode(token)).thenReturn(mockJwt);

        // When
        UUID actualUuid = jwtProvider.getUserIdFromToken(token, true);

        // Then
        assertNotNull(actualUuid);
        assertEquals(expectedUuid, actualUuid);
    }

    @Test
    @DisplayName("Должен вернуть null, если декодер выбросил исключение (невалидный токен)")
    public void getUserIdFromToken_InvalidToken_ShouldReturnNull() {
        // Given
        String token = "invalid-token";
        Mockito.when(jwtDecoder.decode(token)).thenThrow(new RuntimeException("Invalid token"));

        // When
        UUID actualUuid = jwtProvider.getUserIdFromToken(token, true);

        // Then
        assertNull(actualUuid);
    }

    @Test
    @DisplayName("Должен вернуть null, если subject в токене не является валидным UUID")
    public void getUserIdFromToken_InvalidUuidFormat_ShouldReturnNull() {
        // Given
        String token = "valid-token-but-invalid-uuid-subject";
        Jwt mockJwt = Mockito.mock(Jwt.class);
        Mockito.when(mockJwt.getSubject()).thenReturn("not-a-uuid-string");
        Mockito.when(jwtDecoder.decode(token)).thenReturn(mockJwt);

        // When
        UUID actualUuid = jwtProvider.getUserIdFromToken(token, true);

        // Then
        assertNull(actualUuid);
    }
}