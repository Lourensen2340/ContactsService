package com.yaskondrichin.ContactsService.controller.DTO;

import com.yaskondrichin.ContactsService.DTO.ContactDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ContactDTOTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void whenAllFieldsValid_thenNoViolations() {
        ContactDTO dto = new ContactDTO();
        dto.setId(1L);
        dto.setName("Даниил");
        dto.setSurname("Кондричин");
        dto.setPhone("+375291112233");
        dto.setEmail("daniil@example.com");

        Set<ConstraintViolation<ContactDTO>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
        assertEquals(1L, dto.getId());
        assertEquals("Даниил", dto.getName());
        assertEquals("Кондричин", dto.getSurname());
        assertEquals("+375291112233", dto.getPhone());
        assertEquals("daniil@example.com", dto.getEmail());
    }

    @Test
    public void whenNameAndPhoneAreBlank_thenViolationsExist() {
        ContactDTO dto = new ContactDTO();
        dto.setName(""); // Генерирует "Имя не может быть пустым"
        dto.setPhone("   "); // Генерирует "Телефон обязателен"
        dto.setEmail("invalid-email"); // Генерирует "Некорректный email"

        Set<ConstraintViolation<ContactDTO>> violations = validator.validate(dto);

        assertEquals(3, violations.size());

        boolean hasNameError = violations.stream().anyMatch(v -> v.getMessage().equals("Имя не может быть пустым"));
        boolean hasPhoneError = violations.stream().anyMatch(v -> v.getMessage().equals("Телефон обязателен"));
        boolean hasEmailError = violations.stream().anyMatch(v -> v.getMessage().equals("Некорректный email"));

        assertTrue(hasNameError);
        assertTrue(hasPhoneError);
        assertTrue(hasEmailError);
    }
}
