package com.yaskondrichin.ContactsService.controller.DTO;

import com.yaskondrichin.ContactsService.DTO.AssignRoleDTO;
import com.yaskondrichin.ContactsService.domain.model.Role;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class AssignRoleDTOTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void whenFieldsAreNull_thenViolationsExist() {
        AssignRoleDTO dto = new AssignRoleDTO(); // Поля изначально null

        Set<ConstraintViolation<AssignRoleDTO>> violations = validator.validate(dto);

        assertEquals(2, violations.size());

        boolean hasUserError = violations.stream().anyMatch(v -> v.getMessage().equals("ID пользователя обязателен"));
        boolean hasRoleError = violations.stream().anyMatch(v -> v.getMessage().equals("Роль обязательна"));

        assertTrue(hasUserError);
        assertTrue(hasRoleError);
    }
}
