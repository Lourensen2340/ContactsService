package com.yaskondrichin.ContactsService.controller.DTO;

import com.yaskondrichin.ContactsService.DTO.RegisterDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class RegisterDTOTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testGettersAndSetters() {
        RegisterDTO dto = new RegisterDTO();
        dto.setUsername("ivan_ivanov");
        dto.setPassword("securePass123");
        dto.setEmail("ivan@mail.com");
        dto.setPhone("+375291112233");

        assertEquals("ivan_ivanov", dto.getUsername());
        assertEquals("securePass123", dto.getPassword());
        assertEquals("ivan@mail.com", dto.getEmail());
        assertEquals("+375291112233", dto.getPhone());
    }

    @Test
    public void testEqualsAndHashCode() {
        RegisterDTO dto1 = new RegisterDTO();
        dto1.setUsername("user");

        RegisterDTO dto2 = new RegisterDTO();
        dto2.setUsername("user");

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    public void whenFieldsAreEmpty_thenNoViolationsExistBecauseNoAnnotations() {
        // Проверяем, что пустой объект не вызывает ошибок, так как аннотаций валидации в DTO нет
        RegisterDTO dto = new RegisterDTO();

        Set<ConstraintViolation<RegisterDTO>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty(), "Ожидалось 0 ошибок, так как аннотации валидации отсутствуют");
    }
}