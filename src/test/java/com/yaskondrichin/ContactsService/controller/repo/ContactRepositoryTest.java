package com.yaskondrichin.ContactsService.controller.repo;

import com.yaskondrichin.ContactsService.domain.model.Contact;
import com.yaskondrichin.ContactsService.domain.model.Login;
import com.yaskondrichin.ContactsService.domain.model.Role;
import com.yaskondrichin.ContactsService.domain.repo.ContactRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ContactRepositoryTest {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Transactional
    @Test
    public void findAllByUserId_WhenContactsExist_ShouldReturnListOfContacts() {
        // 1. Создаем пользователя, передавая null вместо жесткого ID
        Login mockUser = new Login(null, "yaskon", "hashed_pass", "yas@example.com", "+375291112233", Role.USER);
        mockUser = entityManager.persistAndFlush(mockUser); // Сохраняем и получаем объект с ID из базы

        // 2. Создаем контакт
        Contact contact = new Contact();
        contact.setId(null); // ИСПРАВЛЕНО: Обязательно null, чтобы база сгенерировала ID сама
        contact.setName("Иван");
        contact.setSurname("Иванов");
        contact.setPhone("+375294445566");
        contact.setEmail("ivan@mail.ru");
        contact.setUser(mockUser); // Привязываем сохраненного пользователя

        entityManager.persistAndFlush(contact);

        // 3. Вызываем репозиторий
        List<Contact> contacts = contactRepository.findAllByUserId(mockUser.getId());

        assertFalse(contacts.isEmpty());
        assertEquals(1, contacts.size());
        assertEquals("Иван", contacts.get(0).getName());
    }

    @Test
    public void findAllByUserId_WhenNoContactsExist_ShouldReturnEmptyList() {
        List<Contact> foundContacts = contactRepository.findAllByUserId(99999L);
        assertNotNull(foundContacts);
        assertTrue(foundContacts.isEmpty());
    }
}
