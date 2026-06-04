package com.yaskondrichin.ContactsService.controller.model;

import com.yaskondrichin.ContactsService.domain.model.Contact;
import com.yaskondrichin.ContactsService.domain.model.Login;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ContactTest {

    private static Validator validator;

    @BeforeAll
    public static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
    @Transactional
    @Test
    public void validContact_ShouldHaveNoValidationViolations() {
        Login mockUser = new Login();
        mockUser.setId(1L);

        Contact contact = new Contact();
        contact.setName("Иван");
        contact.setSurname("Иванов");
        contact.setPhone("+375291112233");
        contact.setEmail("ivanov@example.com");
        contact.setUser(mockUser);

        Set<ConstraintViolation<Contact>> violations = validator.validate(contact);
        assertTrue(violations.isEmpty(), "Валидный контакт не должен вызывать ошибок валидации");
    }
    @Transactional
    @Test
    public void contact_WhenFieldsAreBlank_ShouldFailValidation() {
        Contact contact = new Contact();
        contact.setName(""); // Ошибка: @NotBlank и @Size(min=2)
        contact.setSurname(" "); // Ошибка: @NotBlank
        contact.setPhone(""); // Ошибка: @NotBlank
        contact.setEmail("invalid-email"); // Ошибка: @Email

        Set<ConstraintViolation<Contact>> violations = validator.validate(contact);
        assertFalse(violations.isEmpty(), "Пустые поля должны вызывать ошибки валидации");

        // Проверяем, что ошибок несколько (как минимум на имя, фамилию, телефон и почту)
        assertTrue(violations.size() >= 4);
    }
    @Transactional
    @Test
    public void contact_WhenNameIsTooShort_ShouldFailValidation() {
        Contact contact = new Contact();
        contact.setName("А"); // Слишком короткое имя (минимум 2 символа)
        contact.setSurname("Петров");
        contact.setPhone("+375295555555");

        Set<ConstraintViolation<Contact>> violations = validator.validate(contact);

        boolean hasSizeViolation = violations.stream()
                .anyMatch(v -> v.getMessage().contains("должно быть длинее 2-х символов"));

        assertTrue(hasSizeViolation, "Имя короче 2 символов должно генерировать ошибку @Size");
    }

    @Test
    public void contactLombok_ShouldWorkCorrectly() {
        Contact c1 = new Contact();
        c1.setId(10L);
        c1.setName("Тест");

        Contact c2 = new Contact();
        c2.setId(10L);
        c2.setName("Тест");

        assertEquals(c1, c2, "Метод equals() от Lombok должен подтвердить равенство объектов с одинаковыми ID и данными");
        assertEquals(c1.hashCode(), c2.hashCode(), "hashCode() должен совпадать для эквивалентных объектов");
        assertNotNull(c1.toString());
    }
}
