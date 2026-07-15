package com.yaskondrichin.ContactsService.controller.repo;

import com.yaskondrichin.ContactsService.domain.model.UserToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class UserTokenRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Должен успешно сохранить UserToken и сгенерировать UUID")
    void shouldSaveUserTokenAndGenerateId() {
        // Given
        UserToken token = UserToken.builder()
                .tokenValue("some-jwt-token-value")
                .userId(UUID.randomUUID())
                .expiryDate(Instant.now().plusSeconds(3600))
                .revoked(false)
                .build();

        // When
        UserToken savedToken = entityManager.persistAndFlush(token);

        // Then
        assertThat(savedToken.getId()).isNotNull(); // Проверяем автогенерацию UUID
        assertThat(savedToken.getTokenValue()).isEqualTo("some-jwt-token-value");
        assertThat(savedToken.isRevoked()).isFalse();
    }

    @Test
    @DisplayName("Должен выбросить исключение, если tokenValue равен null (нарушение nullable = false)")
    void shouldThrowExceptionWhenTokenValueIsNull() {
        // Given
        UserToken token = UserToken.builder()
                .tokenValue(null) // Нарушение ограничения
                .userId(UUID.randomUUID())
                .expiryDate(Instant.now().plusSeconds(3600))
                .revoked(false)
                .build();

        // When & Then
        assertThatThrownBy(() -> entityManager.persistAndFlush(token))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Должен выбросить исключение при попытке сохранить дубликат tokenValue (нарушение unique = true)")
    void shouldThrowExceptionWhenTokenValueIsDuplicate() {
        // Given
        String duplicateValue = "duplicate-token";
        UUID user1 = UUID.randomUUID();
        UUID user2 = UUID.randomUUID();

        UserToken token1 = UserToken.builder()
                .tokenValue(duplicateValue)
                .userId(user1)
                .expiryDate(Instant.now().plusSeconds(3600))
                .revoked(false)
                .build();

        UserToken token2 = UserToken.builder()
                .tokenValue(duplicateValue) // Тот же токен
                .userId(user2)
                .expiryDate(Instant.now().plusSeconds(3600))
                .revoked(false)
                .build();

        // When
        entityManager.persistAndFlush(token1);

        // Then
        assertThatThrownBy(() -> entityManager.persistAndFlush(token2))
                .isInstanceOf(RuntimeException.class);
    }
}
