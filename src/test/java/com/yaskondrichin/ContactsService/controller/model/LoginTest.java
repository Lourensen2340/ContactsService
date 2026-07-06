package com.yaskondrichin.ContactsService.controller.model;

import com.yaskondrichin.ContactsService.domain.model.Login;
import com.yaskondrichin.ContactsService.domain.enums.Role;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class LoginTest {

    @Test
    public void loginAllArgsConstructor_ShouldInitializeAllFields() {
        UUID mockId = UUID.randomUUID(); // Исправлено: заменено с Long на UUID

        // Передаем все 8 параметров согласно структуре объявленных полей в Login
        Login login = new Login(
                new ArrayList<>(),   // contacts (List<Contact>)
                mockId,              // id (UUID)
                "admin",             // login (String)
                "password123",       // pass (String)
                "admin@example.com", // email (String)
                "+375290000000",     // phone (String)
                Role.ROLE_ADMIN,     // role (Role)
                "rawPassword123"     // rawPassword (String) - ДОБАВЛЕНО
        );

        assertNotNull(login.getContacts(), "Список контактов не должен быть null");
        assertEquals(mockId, login.getId());
        assertEquals("admin", login.getLogin());
        assertEquals("password123", login.getPass());
        assertEquals("admin@example.com", login.getEmail());
        assertEquals("+375290000000", login.getPhone());
        assertEquals(Role.ROLE_ADMIN, login.getRole());
        assertEquals("rawPassword123", login.getRawPassword()); // Добавлено утверждение для проверки rawPassword
    }

    @Test
    public void loginLombokMethods_ShouldBehaveCorrectly() {
        UUID mockId = UUID.randomUUID(); // Исправлено: заменено с Long на UUID

        Login login1 = new Login();
        login1.setId(mockId);
        login1.setLogin("user");
        login1.setRole(Role.USER);

        Login login2 = new Login();
        login2.setId(mockId);
        login2.setLogin("user");
        login2.setRole(Role.USER);

        assertEquals(login1, login2, "Метод equals() от Lombok должен подтвердить идентичность объектов с одинаковыми полями");
        assertEquals(login1.hashCode(), login2.hashCode(), "Методы hashCode() должны возвращать одинаковые значения");
    }
}
