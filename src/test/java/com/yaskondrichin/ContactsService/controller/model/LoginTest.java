package com.yaskondrichin.ContactsService.controller.model;


import com.yaskondrichin.ContactsService.domain.model.Login;
import com.yaskondrichin.ContactsService.domain.model.Role;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class LoginTest {

    @Test
    public void loginAllArgsConstructor_ShouldInitializeAllFields() {
        // Исправлено: передаем пустой список ArrayList первым аргументом,
        // так как поле contacts идет первым в объявлении класса Login
        Login login = new Login(
                new ArrayList<>(),   // contacts (List<Contact>)
                1L,                  // id (Long)
                "admin",             // login (String)
                "password123",       // pass (String)
                "admin@example.com", // email (String)
                "+375290000000",     // phone (String)
                Role.ROLE_ADMIN      // role (Role)
        );

        assertNotNull(login.getContacts(), "Список контактов не должен быть null");
        assertEquals(1L, login.getId());
        assertEquals("admin", login.getLogin());
        assertEquals("password123", login.getPass());
        assertEquals("admin@example.com", login.getEmail());
        assertEquals("+375290000000", login.getPhone());
        assertEquals(Role.ROLE_ADMIN, login.getRole());
    }

    @Test
    public void loginLombokMethods_ShouldBehaveCorrectly() {
        Login login1 = new Login();
        login1.setId(5L);
        login1.setLogin("user");
        login1.setRole(Role.USER);

        Login login2 = new Login();
        login2.setId(5L);
        login2.setLogin("user");
        login2.setRole(Role.USER);

        // Проверяем корректность работы @Data (equals и hashCode)
        assertEquals(login1, login2, "Метод equals() от Lombok должен подтвердить идентичность объектов с одинаковыми полями");
        assertEquals(login1.hashCode(), login2.hashCode(), "Методы hashCode() должны возвращать одинаковые значения");
    }
}
