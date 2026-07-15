package com.yaskondrichin.ContactsService.controller.service.impl;

import com.yaskondrichin.ContactsService.DTO.TokenValidationDTO;
import com.yaskondrichin.ContactsService.Mapper.TokenMapper;
import com.yaskondrichin.ContactsService.domain.model.UserToken;
import com.yaskondrichin.ContactsService.domain.repo.UserTokenRepository;
import com.yaskondrichin.ContactsService.service.impl.TokenValidationServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenValidationServiceImplTest {

    @Mock
    private UserTokenRepository tokenRepository;

    @Mock
    private TokenMapper tokenMapper;

    @InjectMocks
    private TokenValidationServiceImpl tokenValidationService;

    @Test
    @DisplayName("Сохранение токена: должен успешно создать и сохранить сущность UserToken")
    void saveToken_ShouldSaveCorrectUserToken() {
        // Given
        String tokenValue = "test-token-value";
        UUID userId = UUID.randomUUID();
        Instant expiryDate = Instant.now().plusSeconds(3600);

        // When
        tokenValidationService.saveToken(tokenValue, userId, expiryDate);

        // Then
        ArgumentCaptor<UserToken> tokenCaptor = ArgumentCaptor.forClass(UserToken.class);
        verify(tokenRepository, times(1)).save(tokenCaptor.capture());

        UserToken savedToken = tokenCaptor.getValue();
        assertNotNull(savedToken);
        assertEquals(tokenValue, savedToken.getTokenValue());
        assertEquals(userId, savedToken.getUserId());
        assertEquals(expiryDate, savedToken.getExpiryDate());
        assertFalse(savedToken.isRevoked()); // По умолчанию должен быть false
    }

    @Test
    @DisplayName("Валидация токена: должен вернуть TokenValidationDTO, если токен найден в репозитории")
    void validateToken_WhenTokenExists_ShouldReturnDto() {
        // Given
        String tokenValue = "existing-token";
        UserToken mockToken = UserToken.builder()
                .tokenValue(tokenValue)
                .userId(UUID.randomUUID())
                .expiryDate(Instant.now().plusSeconds(3600))
                .revoked(false)
                .build();

        TokenValidationDTO mockDto = new TokenValidationDTO();
        mockDto.setUserId(mockToken.getUserId());
        mockDto.setValid(true);

        when(tokenRepository.findByTokenValue(tokenValue)).thenReturn(Optional.of(mockToken));
        when(tokenMapper.toDto(mockToken)).thenReturn(mockDto);

        // When
        TokenValidationDTO result = tokenValidationService.validateToken(tokenValue);

        // Then
        assertNotNull(result);
        assertTrue(result.isValid());
        assertEquals(mockToken.getUserId(), result.getUserId());
        verify(tokenRepository, times(1)).findByTokenValue(tokenValue);
        verify(tokenMapper, times(1)).toDto(mockToken);
    }

    @Test
    @DisplayName("Валидация токена: должен выбросить RuntimeException, если токен не найден")
    void validateToken_WhenTokenDoesNotExist_ShouldThrowException() {
        // Given
        String tokenValue = "missing-token";
        when(tokenRepository.findByTokenValue(tokenValue)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                tokenValidationService.validateToken(tokenValue)
        );

        assertEquals("Токен не найден в белом списке", exception.getMessage());
        verify(tokenRepository, times(1)).findByTokenValue(tokenValue);
        verifyNoInteractions(tokenMapper);
    }

    @Test
    @DisplayName("Отзыв токена: должен перевести revoked в true и сохранить токен, если он найден")
    void revokeToken_WhenTokenExists_ShouldSetRevokedToTrueAndSave() {
        // Given
        String tokenValue = "token-to-revoke";
        UserToken mockToken = UserToken.builder()
                .tokenValue(tokenValue)
                .userId(UUID.randomUUID())
                .revoked(false)
                .build();

        when(tokenRepository.findByTokenValue(tokenValue)).thenReturn(Optional.of(mockToken));

        // When
        tokenValidationService.revokeToken(tokenValue);

        // Then
        assertTrue(mockToken.isRevoked(), "Поле revoked должно стать true");
        verify(tokenRepository, times(1)).findByTokenValue(tokenValue);
        verify(tokenRepository, times(1)).save(mockToken);
    }

    @Test
    @DisplayName("Отзыв токена: должен выбросить RuntimeException, если токен для отзыва не найден")
    void revokeToken_WhenTokenDoesNotExist_ShouldThrowException() {
        // Given
        String tokenValue = "missing-token";
        when(tokenRepository.findByTokenValue(tokenValue)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                tokenValidationService.revokeToken(tokenValue)
        );

        assertEquals("Токен не найден", exception.getMessage());
        verify(tokenRepository, times(1)).findByTokenValue(tokenValue);
        verify(tokenRepository, never()).save(any(UserToken.class));
    }

    @Test
    @DisplayName("Отзыв всех токенов: должен вызвать удаление всех токенов пользователя по userId")
    void revokeAllUserTokens_ShouldInvokeRepositoryDelete() {
        // Given
        UUID userId = UUID.randomUUID();

        // When
        tokenValidationService.revokeAllUserTokens(userId);

        // Then
        verify(tokenRepository, times(1)).deleteByUserId(userId);
    }
}
