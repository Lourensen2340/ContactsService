package com.yaskondrichin.ContactsService.controller.repo;

import com.yaskondrichin.ContactsService.domain.model.Contact;
import com.yaskondrichin.ContactsService.domain.model.Login;
import com.yaskondrichin.ContactsService.domain.enums.Role;
import com.yaskondrichin.ContactsService.domain.repo.ContactRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ContactRepositoryTest {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Transactional
    @Test
    public void findAllByLoginId_WhenContactsExist_ShouldReturnListOfContacts() {
        // 1. Создаем и сохраняем аккаунт владельца контактов через сеттеры
        Login mockUser = new Login();
        mockUser.setLogin("yaskon");
        mockUser.setPass("hashed_pass");
        mockUser.setEmail("yas@example.com");
        mockUser.setPhone("+375291112233");
        mockUser.setRole(Role.USER);
        mockUser.setContacts(new ArrayList<>());
        mockUser = entityManager.persistAndFlush(mockUser);

        // 2. Создаем контакт
        Contact contact = new Contact();
        contact.setName("Иван");
        contact.setSurname("Иванов");
        contact.setPhone("+375294445566");
        contact.setEmail("ivan@mail.ru");

        // Исправлено: устанавливаем прямую связь с Login вместо обращения к несуществующей коллекции users
        contact.setLogin(mockUser);

        entityManager.persistAndFlush(contact);

        // 3. Вызываем репозиторий через актуальный метод поиска по LoginId
        List<Contact> contacts = contactRepository.findAllByLoginId(mockUser.getId());

        assertFalse(contacts.isEmpty());
        assertEquals(1, contacts.size());
        assertEquals("Иван", contacts.get(0).getName());
    }

    @Test
    public void findAllByLoginId_WhenNoContactsExist_ShouldReturnEmptyList() {
        // Исправлено: тип аргумента заменен с Long (999L) на UUID
        List<Contact> contacts = contactRepository.findAllByLoginId(UUID.randomUUID());
        assertTrue(contacts.isEmpty());
    }
}
