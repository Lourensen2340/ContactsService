package com.yaskondrichin.ContactsService.controller.config;

import com.yaskondrichin.ContactsService.config.PasswordEncoderConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = PasswordEncoderConfig.class)
class PasswordEncoderConfigTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("Бин PasswordEncoder должен быть успешно создан и иметь тип BCryptPasswordEncoder")
    void beanShouldBeCreatedAndBeBCrypt() {
        assertThat(passwordEncoder).isNotNull();
        assertThat(passwordEncoder).isInstanceOf(BCryptPasswordEncoder.class);
    }

    @Test
    @DisplayName("Кодировщик должен успешно хэшировать и проверять соответствие паролей")
    void passwordEncoderShouldEncodeAndMatchSuccessfully() {
        // Given
        String rawPassword = "my-secure-password-123";

        // When - кодируем пароль
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // Then - хэш не должен быть пустым и не должен быть равен исходному паролю
        assertThat(encodedPassword).isNotBlank();
        assertThat(encodedPassword).isNotEqualTo(rawPassword);

        // Then - проверка соответствия (match) должна пройти успешно
        assertThat(passwordEncoder.matches(rawPassword, encodedPassword)).isTrue();
    }

    @Test
    @DisplayName("Проверка соответствия должна возвращать false для неверного пароля")
    void passwordEncoderShouldNotMatchWrongPassword() {
        // Given
        String rawPassword = "my-secure-password-123";
        String wrongPassword = "wrong-password";

        // When
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // Then
        assertThat(passwordEncoder.matches(wrongPassword, encodedPassword)).isFalse();
    }
}