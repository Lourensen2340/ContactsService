package com.yaskondrichin.ContactsService.controller.Mapper;

import com.yaskondrichin.ContactsService.DTO.TokenValidationDTO;
import com.yaskondrichin.ContactsService.Mapper.TokenMapper;
import com.yaskondrichin.ContactsService.domain.model.UserToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TokenMapperTest {

    // Инициализируем маппер через фабрику MapStruct для чистых Unit-тестов
    private final TokenMapper tokenMapper = Mappers.getMapper(TokenMapper.class);

    @Test
    @DisplayName("Должен успешно смаппить все базовые поля из UserToken в TokenValidationDTO")
    void shouldMapAllBasicFieldsCorrectly() {
        // Given
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String tokenValue = "some-jwt-token-string";
        Instant expiryDate = Instant.now().plusSeconds(1800); // Действителен еще 30 минут

        UserToken entity = UserToken.builder()
                .id(id)
                .tokenValue(tokenValue)
                .userId(userId)
                .expiryDate(expiryDate)
                .revoked(false)
                .build();

        // When
        TokenValidationDTO dto = tokenMapper.toDto(entity);

        // Then
        assertNotNull(dto);
        assertEquals(id, dto.getId());
        assertEquals(userId, dto.getUserId());
        assertEquals(expiryDate, dto.getExpiryDate());
        assertFalse(dto.isRevoked());
        assertTrue(dto.isValid(), "Поле isValid должно быть true, так как токен не отозван и время жизни не истекло");
    }

    @Test
    @DisplayName("Должен установить isValid в false, если токен отозван (revoked = true)")
    void shouldSetIsValidToFalseWhenTokenIsRevoked() {
        // Given
        UserToken entity = UserToken.builder()
                .id(UUID.randomUUID())
                .tokenValue("token")
                .userId(UUID.randomUUID())
                .expiryDate(Instant.now().plusSeconds(1800))
                .revoked(true) // Отозван
                .build();

        // When
        TokenValidationDTO dto = tokenMapper.toDto(entity);

        // Then
        assertNotNull(dto);
        assertTrue(dto.isRevoked());
        assertFalse(dto.isValid(), "Поле isValid должно быть false для отозванного токена");
    }

    @Test
    @DisplayName("Должен установить isValid в false, если срок действия токена истек")
    void shouldSetIsValidToFalseWhenTokenIsExpired() {
        // Given
        UserToken entity = UserToken.builder()
                .id(UUID.randomUUID())
                .tokenValue("token")
                .userId(UUID.randomUUID())
                .expiryDate(Instant.now().minusSeconds(10)) // Истек 10 секунд назад
                .revoked(false)
                .build();

        // When
        TokenValidationDTO dto = tokenMapper.toDto(entity);

        // Then
        assertNotNull(dto);
        assertFalse(dto.isRevoked());
        assertFalse(dto.isValid(), "Поле isValid должно быть false для просроченного токена");
    }

    @Test
    @DisplayName("Должен корректно вернуть false при расчете isValid для null-объекта")
    void shouldReturnFalseForIsValidWhenTokenEntityIsNull() {
        // When
        boolean isValid = tokenMapper.calculateIsValid(null);

        // Then
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Должен вернуть null при маппинге null-сущности")
    void shouldReturnNullWhenMappingNullEntity() {
        // When
        TokenValidationDTO dto = tokenMapper.toDto(null);

        // Then
        assertNull(dto);
    }
}
