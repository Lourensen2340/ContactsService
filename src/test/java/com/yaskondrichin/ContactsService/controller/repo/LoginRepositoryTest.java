package com.yaskondrichin.ContactsService.controller.repo;

import com.yaskondrichin.ContactsService.domain.model.Login;
import com.yaskondrichin.ContactsService.domain.enums.Role;
import com.yaskondrichin.ContactsService.domain.repo.LoginRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class LoginRepositoryTest {

    @Autowired
    private LoginRepository loginRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void findByLogin_WhenLoginExists_ShouldReturnOptionalWithLogin() {
        Login loginEntity = new Login();
        loginEntity.setLogin("unique_user");
        loginEntity.setPass("pass");
        loginEntity.setEmail("unique@mail.com");
        loginEntity.setPhone("+375291234567");
        loginEntity.setRole(Role.USER);

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
    void findFirstByOrderByIdDesc_ShouldReturnLastSavedRecord() {
        // 1. Полностью очищаем репозиторий перед тестом
        loginRepository.deleteAll();

        // 2. Создаем и сохраняем первую запись (ID сгенерируется автоматически)
        Login first = new Login();
        first.setLogin("firstUser");
        first.setPass("pass1");
        first.setEmail("first@mail.com");
        first.setPhone("+375291111111");
        first.setRole(Role.USER);
        first = loginRepository.saveAndFlush(first); // Сохраняем и переприсваиваем объект с ID

        // 3. Создаем и сохраняем вторую запись (ID сгенерируется автоматически)
        Login second = new Login();
        second.setLogin("secondUser");
        second.setPass("pass2");
        second.setEmail("second@mail.com");
        second.setPhone("+375292222222");
        second.setRole(Role.USER);
        second = loginRepository.saveAndFlush(second); // Сохраняем и переприсваиваем объект с ID

        // 4. Динамически определяем, какой из сгенерированных UUID больше
        // Метод findFirstByOrderByIdDesc() обязан вернуть запись именно с максимальным UUID
        Login expectedMax = first.getId().compareTo(second.getId()) > 0 ? first : second;

        // 5. Вызываем метод репозитория
        Optional<Login> result = loginRepository.findFirstByOrderByIdDesc();

        // 6. Проверяем корректность работы сортировки базы данных
        assertTrue(result.isPresent());
        assertEquals(expectedMax.getLogin(), result.get().getLogin(),
                "Метод должен возвращать запись с наибольшим ID согласно сортировке DESC");
    }
}