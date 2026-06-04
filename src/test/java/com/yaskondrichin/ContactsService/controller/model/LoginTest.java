package com.yaskondrichin.ContactsService.controller.model;


import com.yaskondrichin.ContactsService.domain.model.Login;
import com.yaskondrichin.ContactsService.domain.model.Role;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

public class LoginTest {
    @Transactional
    @Test
    public void loginAllArgsConstructor_ShouldInitializeAllFields() {
        Login login = new Login(1L, "admin", "password123", "admin@example.com", "+375290000000", Role.ROLE_ADMIN);

        assertEquals(1L, login.getId());
        assertEquals("admin", login.getLogin());
        assertEquals("password123", login.getPass());
        assertEquals("admin@example.com", login.getEmail());
        assertEquals("+375290000000", login.getPhone());
        assertEquals(Role.ROLE_ADMIN, login.getRole());
    }
    @Transactional
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

        assertEquals(login1, login2);
        assertEquals(login1.hashCode(), login2.hashCode());
        assertTrue(login1.toString().contains("user"));
    }
}
