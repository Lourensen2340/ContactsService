package com.yaskondrichin.ContactsService.controller.model;

import com.yaskondrichin.ContactsService.domain.enums.Role;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

public class RoleTest {
    @Transactional
    @Test
    public void roleEnum_ShouldContainAllRequiredRoles() {
        // Гарантируем наличие ролей для системы авторизации
        // Изменено с "ROLE_USER" на "USER", так как именно так роль объявлена в модели Role.java
        assertNotNull(Role.valueOf("USER"));
        assertNotNull(Role.valueOf("ROLE_ADMIN"));
    }

    @Transactional
    @Test
    public void roleEnumValues_ShouldMatchExpectedCount() {
        Role[] roles = Role.values();
        assertEquals(2, roles.length, "Enum Role должен содержать ровно 2 роли (USER и ROLE_ADMIN)");
    }
}
