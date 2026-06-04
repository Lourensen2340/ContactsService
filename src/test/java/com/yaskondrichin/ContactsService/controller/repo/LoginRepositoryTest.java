package com.yaskondrichin.ContactsService.controller.repo;

import com.yaskondrichin.ContactsService.domain.model.Login;
import com.yaskondrichin.ContactsService.domain.model.Role;
import com.yaskondrichin.ContactsService.domain.repo.LoginRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class LoginRepositoryTest {
    @Autowired
    private LoginRepository loginRepository;
    @Autowired
    private TestEntityManager entityManager;
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    @Test
    public void findByLogin_WhenLoginExists_ShouldReturnOptionalWithLogin() {
        Login loginEntity = new Login(null, "unique_user", "pass", "unique@mail.com", "+375291234567", Role.USER);
        entityManager.persistAndFlush(loginEntity);

        Optional<Login> found = loginRepository.findByLogin("unique_user");

        assertTrue(found.isPresent());
        assertEquals("unique_user", found.get().getLogin());
        assertEquals("unique@mail.com", found.get().getEmail());
    }

    @Test
    public void findByLogin_WhenLoginDoesNotExist_ShouldReturnEmptyOptional() {
        Optional<Login> found = loginRepository.findByLogin("non_existent_login");
        assertTrue(found.isEmpty());
    }

    @Test
    public void findFirstByOrderByIdDesc_ShouldReturnLastSavedRecord() {
        Login first = new Login(null, "first", "pass", "first@mail.com", "+375291111111", Role.USER);
        Login second = new Login(null, "second", "pass", "second@mail.com", "+375292222222", Role.USER);

        entityManager.persist(first);
        entityManager.persist(second);
        entityManager.flush();

        Optional<Login> lastSaved = loginRepository.findFirstByOrderByIdDesc();

        assertTrue(lastSaved.isPresent());
        assertEquals("second", lastSaved.get().getLogin(), "Метод должен возвращать запись с наибольшим ID");
    }
}
