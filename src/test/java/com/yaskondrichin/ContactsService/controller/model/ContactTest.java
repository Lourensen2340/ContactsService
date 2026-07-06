package com.yaskondrichin.ContactsService.controller.model;

import com.yaskondrichin.ContactsService.domain.model.Contact;
import com.yaskondrichin.ContactsService.domain.model.Login;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ContactTest {

    private static Validator validator;

    @BeforeAll
    public static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void validContact_ShouldHaveNoValidationViolations() {
        Login mockLogin = new Login();
        mockLogin.setId(UUID.randomUUID());

        Contact contact = new Contact();
        contact.setName("Иван");
        contact.setSurname("Иванов");
        contact.setPhone("+375291112233");
        contact.setEmail("ivanov@example.com");
        contact.setLogin(mockLogin); // Исправлено: заменено с setUser(mockUser)

        Set<ConstraintViolation<Contact>> violations = validator.validate(contact);
        assertTrue(violations.isEmpty(), "У валидного контакта не должно быть ошибок валидации");
    }

    @Test
    public void whenFieldsAreBlank_thenViolationsExist() {
        Contact contact = new Contact();

        Set<ConstraintViolation<Contact>> violations = validator.validate(contact);
        assertFalse(violations.isEmpty(), "Пустые поля должны вызывать ошибки валидации");
        assertTrue(violations.size() >= 3, "Должно быть минимум 3 ошибки валидации для пустых обязательных полей");
    }

    @Test
    public void contact_WhenNameIsTooShort_ShouldFailValidation() {
        Contact contact = new Contact();
        contact.setName("А");
        contact.setSurname("Петров");
        contact.setPhone("+375295555555");

        Set<ConstraintViolation<Contact>> violations = validator.validate(contact);

        boolean hasNameViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("name"));

        assertTrue(hasNameViolation, "Короткое имя должно генерировать ошибку валидации поля name");
    }

    @Test
    public void contact_WhenSurnameIsBlank_ShouldReturnSpecificErrorMessage() {
        Contact contact = new Contact();
        contact.setName("Иван");
        contact.setSurname("");
        contact.setPhone("+375295555555");

        Set<ConstraintViolation<Contact>> violations = validator.validate(contact);

        boolean hasSurnameViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("surname"));

        assertTrue(hasSurnameViolation, "Пустая фамилия должна генерировать ошибку валидации поля surname");
    }

    @Test
    public void contactLombok_ShouldWorkCorrectly() {
        Contact c1 = new Contact();
        UUID mockId = UUID.randomUUID(); // Исправлено: заменено с Long на UUID
        c1.setId(mockId);
        c1.setName("Тест");

        Contact c2 = new Contact();
        c2.setId(mockId);
        c2.setName("Тест");

        assertEquals(c1, c2, "Метод equals() от Lombok должен подтвердить идентичность объектов с одинаковыми ID и полями");
        assertEquals(c1.hashCode(), c2.hashCode(), "Методы hashCode() должны возвращать одинаковые значения");
    }
}