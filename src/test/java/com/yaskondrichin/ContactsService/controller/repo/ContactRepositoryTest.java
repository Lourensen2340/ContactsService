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

import java.util.ArrayList;
import java.util.List;

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
        // 1. Создаем и сохраняем пользователя (без лишних слэшей)
        Login mockUser = new Login(new ArrayList<>(), null, "yaskon", "hashed_pass", "yas@example.com", "+375291112233", Role.USER);
        mockUser = entityManager.persistAndFlush(mockUser);

        // 2. Создаем контакт
        Contact contact = new Contact();
        contact.setId(null);
        contact.setName("Иван");
        contact.setSurname("Иванов");
        contact.setPhone("+375294445566");
        contact.setEmail("ivan@mail.ru");

        // Исправлено: добавляем Login в список users, так как репозиторий делает JOIN c.users
        contact.getUsers().add(mockUser);

        entityManager.persistAndFlush(contact);

        // 3. Вызываем репозиторий
        List<Contact> contacts = contactRepository.findAllByUserIdAndIsDeletedFalse(mockUser.getId());

        assertFalse(contacts.isEmpty());
        assertEquals(1, contacts.size());
        assertEquals("Иван", contacts.get(0).getName());
    }

    @Test
    public void findAllByUserId_WhenNoContactsExist_ShouldReturnEmptyList() {
        List<Contact> contacts = contactRepository.findAllByUserIdAndIsDeletedFalse(999L);
        assertTrue(contacts.isEmpty());
    }
}
