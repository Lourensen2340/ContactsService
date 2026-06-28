package com.yaskondrichin.ContactsService.controller.DTO;

import com.yaskondrichin.ContactsService.DTO.LoginRequestDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class LoginRequestDTOTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void whenValidData_thenNoViolations() {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setLogin("user"); // ИСПРАВЛЕНО: используем setLogin вместо setUsername
        dto.setEmail("user@mail.com");
        dto.setPassword("secret123");
        dto.setPhone("+375291234567");

        Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    public void whenPhoneIsBlank_thenBlankViolation() {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setPhone(""); // Нарушает и @NotBlank, и @Pattern

        Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);

        boolean hasBlankMessage = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Номер телефона не должен быть пустым"));

        assertTrue(hasBlankMessage);
    }

    @Test
    public void whenPhoneInvalidPattern_thenPatternViolation() {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setPhone("80291234567"); // Не соответствует формату +375XXXXXXXXX

        Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);

        assertEquals(1, violations.size());
        assertEquals("Номер телефона должен быть в формате +375XXXXXXXXX",
                violations.iterator().next().getMessage());
    }
}
