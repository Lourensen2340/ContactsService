package com.yaskondrichin.ContactsService.controller.model;

import com.yaskondrichin.ContactsService.domain.model.UserToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserTokenTest {

    @Test
    @DisplayName("Тестирование Builder и Getter-ов")
    void testUserTokenBuilderAndGetters() {
        // Given
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String tokenValue = "test.jwt.token";
        Instant expiry = Instant.now().plusSeconds(1800);

        // When
        UserToken userToken = UserToken.builder()
                .id(id)
                .tokenValue(tokenValue)
                .userId(userId)
                .expiryDate(expiry)
                .revoked(true)
                .build();

        // Then
        assertEquals(id, userToken.getId());
        assertEquals(tokenValue, userToken.getTokenValue());
        assertEquals(userId, userToken.getUserId());
        assertEquals(expiry, userToken.getExpiryDate());
        assertTrue(userToken.isRevoked());
    }

    @Test
    @DisplayName("Тестирование Setter-ов")
    void testUserTokenSetters() {
        // Given
        UserToken userToken = new UserToken();
        UUID userId = UUID.randomUUID();
        Instant expiry = Instant.now();

        // When
        userToken.setId(userId);
        userToken.setTokenValue("new-token");
        userToken.setUserId(userId);
        userToken.setExpiryDate(expiry);
        userToken.setRevoked(false);

        // Then
        assertEquals(userId, userToken.getId());
        assertEquals("new-token", userToken.getTokenValue());
        assertEquals(userId, userToken.getUserId());
        assertEquals(expiry, userToken.getExpiryDate());
        assertFalse(userToken.isRevoked());
    }

    @Test
    @DisplayName("Тестирование конструкторов NoArgsConstructor и AllArgsConstructor")
    void testConstructors() {
        // NoArgsConstructor
        UserToken emptyToken = new UserToken();
        assertNull(emptyToken.getId());
        assertNull(emptyToken.getTokenValue());

        // AllArgsConstructor
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Instant now = Instant.now();

        UserToken fullToken = new UserToken(id, "token-123", userId, now, false);

        assertEquals(id, fullToken.getId());
        assertEquals("token-123", fullToken.getTokenValue());
        assertEquals(userId, fullToken.getUserId());
        assertEquals(now, fullToken.getExpiryDate());
        assertFalse(fullToken.isRevoked());
    }
}
