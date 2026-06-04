package com.yaskondrichin.ContactsService.controller.repo;

import com.yaskondrichin.ContactsService.domain.model.User;
import com.yaskondrichin.ContactsService.domain.repo.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Transactional
    @Test
    public void findByUsername_WhenUserExists_ShouldReturnUser() {
        // Передаем null вместо 1L, чтобы генератор IDENTITY отработал корректно
        User user = new User(null, "developer", "secure_pass", "dev@java.by");

        // Сохраняем объект, Hibernate сам присвоит ему ID
        user = entityManager.persistAndFlush(user);

        Optional<User> found = userRepository.findByUsername("developer");

        assertTrue(found.isPresent());
        assertEquals("developer", found.get().getUsername());
        assertEquals("dev@java.by", found.get().getEmail());
    }

    @Transactional
    @Test
    public void findByUsername_WhenUserDoesNotExist_ShouldReturnEmptyOptional() {
        Optional<User> found = userRepository.findByUsername("unknown_dev");
        assertTrue(found.isEmpty());
    }
}
