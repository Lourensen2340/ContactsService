package com.yaskondrichin.ContactsService.controller.model;

import com.yaskondrichin.ContactsService.domain.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {
    @Transactional
    @Test
    public void userNoArgsConstructorAndSetters_ShouldWorkCorrectly() {
        User user = new User();
        user.setId(100L);
        user.setUsername("danila");
        user.setPassword("hashed_pass");
        user.setEmail("danila@example.com");

        assertEquals(100L, user.getId());
        assertEquals("danila", user.getUsername());
        assertEquals("hashed_pass", user.getPassword());
        assertEquals("danila@example.com", user.getEmail());
    }
    @Transactional
    @Test
    public void userEqualsAndHashCode_ShouldBeValid() {
        User user2 = new User(null, "username", "pass", "email@mail.com");
        User user1 = new User(null, "username", "pass", "email@mail.com");
        User user3 = new User(null, "other", "pass", "email@mail.com");

        assertEquals(user1, user2);
        assertNotEquals(user1, user3);
        assertEquals(user1.hashCode(), user2.hashCode());
    }
}
