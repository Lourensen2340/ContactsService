package com.yaskondrichin.ContactsService.controller.DTO;

import com.yaskondrichin.ContactsService.DTO.TokenValidationDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TokenValidationDTOTest {

    @Test
    @DisplayName("Тест геттеров, сеттеров и структуры данных")
    void testGettersAndSetters() {
        // Given
        TokenValidationDTO dto = new TokenValidationDTO();
        UUID tokenId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Instant expiry = Instant.now().plusSeconds(3600);

        // When
        dto.setId(tokenId);
        dto.setUserId(userId);
        dto.setExpiryDate(expiry);
        dto.setRevoked(true);
        dto.setValid(false);

        // Then
        assertEquals(tokenId, dto.getId());
        assertEquals(userId, dto.getUserId());
        assertEquals(expiry, dto.getExpiryDate());
        assertTrue(dto.isRevoked());
        assertFalse(dto.isValid());
    }

    @Test
    @DisplayName("Тест логики вычисляемого поля isValid (валидный токен)")
    void testIsValid_WhenActiveAndNotExpired() {
        // Given
        TokenValidationDTO dto = new TokenValidationDTO();
        Instant futureExpiry = Instant.now().plusSeconds(600); // Действителен еще 10 минут

        // When
        dto.setRevoked(false);
        dto.setExpiryDate(futureExpiry);

        // Моделируем логику: !revoked && expiryDate.isAfter(now)
        boolean calculatedValid = !dto.isRevoked() && dto.getExpiryDate().isAfter(Instant.now());
        dto.setValid(calculatedValid);

        // Then
        assertTrue(dto.isValid(), "Токен должен быть валидным, если он не отозван и время жизни не истекло");
    }

    @Test
    @DisplayName("Тест логики вычисляемого поля isValid (отозванный токен)")
    void testIsValid_WhenRevoked() {
        // Given
        TokenValidationDTO dto = new TokenValidationDTO();
        Instant futureExpiry = Instant.now().plusSeconds(600);

        // When
        dto.setRevoked(true); // Отозван
        dto.setExpiryDate(futureExpiry);

        boolean calculatedValid = !dto.isRevoked() && dto.getExpiryDate().isAfter(Instant.now());
        dto.setValid(calculatedValid);

        // Then
        assertFalse(dto.isValid(), "Токен не должен быть валидным, если он помечен как revoked");
    }

    @Test
    @DisplayName("Тест логики вычисляемого поля isValid (просроченный токен)")
    void testIsValid_WhenExpired() {
        // Given
        TokenValidationDTO dto = new TokenValidationDTO();
        Instant pastExpiry = Instant.now().minusSeconds(10); // Истек 10 секунд назад

        // When
        dto.setRevoked(false);
        dto.setExpiryDate(pastExpiry);

        boolean calculatedValid = !dto.isRevoked() && dto.getExpiryDate().isAfter(Instant.now());
        dto.setValid(calculatedValid);

        // Then
        assertFalse(dto.isValid(), "Токен не должен быть валидным, если время его жизни истекло");
    }

    @Test
    @DisplayName("Тест методов equals, hashCode и toString от Lombok")
    void testEqualsHashCodeAndToString() {
        // Given
        UUID tokenId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Instant expiry = Instant.now().plusSeconds(3600);

        TokenValidationDTO dto1 = new TokenValidationDTO();
        dto1.setId(tokenId);
        dto1.setUserId(userId);
        dto1.setExpiryDate(expiry);
        dto1.setRevoked(false);
        dto1.setValid(true);

        TokenValidationDTO dto2 = new TokenValidationDTO();
        dto2.setId(tokenId);
        dto2.setUserId(userId);
        dto2.setExpiryDate(expiry);
        dto2.setRevoked(false);
        dto2.setValid(true);

        // Then
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotNull(dto1.toString());
        assertTrue(dto1.toString().contains("TokenValidationDTO"));
    }
}