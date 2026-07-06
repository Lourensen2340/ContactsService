package com.yaskondrichin.ContactsService.controller.DTO;

import com.yaskondrichin.ContactsService.DTO.ContactDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

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
        UUID mockId = UUID.randomUUID();
        dto.setId(mockId);
        dto.setName("Даниил");
        dto.setSurname("Кондричин");
        dto.setPhone("+375291112233");
        dto.setEmail("daniil@example.com");

        Set<ConstraintViolation<ContactDTO>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
        assertEquals(mockId, dto.getId());
        assertEquals("Даниил", dto.getName());
        assertEquals("Кондричин", dto.getSurname());
        assertEquals("+375291112233", dto.getPhone());
        assertEquals("daniil@example.com", dto.getEmail());
    }

    @Test
    public void whenFieldsAreInvalid_thenViolationsExist() {
        ContactDTO dto = new ContactDTO();
        dto.setName("");
        dto.setSurname("Кондричин");
        dto.setPhone("   ");
        dto.setEmail("invalid-email");

        Set<ConstraintViolation<ContactDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty(), "Должны быть ошибки валидации");

        boolean hasNameError = violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name"));
        boolean hasPhoneError = violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("phone"));
        boolean hasEmailError = violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email"));

        assertTrue(hasNameError, "Должна быть ошибка валидации для поля name");
        assertTrue(hasPhoneError, "Должна быть ошибка валидации для поля phone");
        assertTrue(hasEmailError, "Должна быть ошибка валидации для поля email");
    }
}