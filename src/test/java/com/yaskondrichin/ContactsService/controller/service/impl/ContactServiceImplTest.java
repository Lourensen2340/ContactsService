package com.yaskondrichin.ContactsService.controller.service.impl;

import com.yaskondrichin.ContactsService.DTO.ContactDTO;
import com.yaskondrichin.ContactsService.domain.model.Contact;
import com.yaskondrichin.ContactsService.domain.model.Login;
import com.yaskondrichin.ContactsService.domain.repo.ContactRepository;
import com.yaskondrichin.ContactsService.domain.repo.LoginRepository;
import com.yaskondrichin.ContactsService.service.impl.ContactServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContactServiceImplTest {

    @Mock
    private ContactRepository contactRepository;

    @Mock
    private LoginRepository loginRepository;

    @InjectMocks
    private ContactServiceImpl contactService;

    private Contact contact;
    private Login owner;
    private ContactDTO contactDTO;

    @BeforeEach
    void setUp() {
        owner = new Login();
        owner.setId(1L);

        contact = new Contact();
        contact.setId(10L);
        contact.setName("Ivan");
        contact.setUser(owner);

        contactDTO = new ContactDTO();
        contactDTO.setName("Ivan");
    }

    @Test
    void save_ShouldCreateNewContact() {
        // Подготовка
        when(loginRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(contactRepository.save(any(Contact.class))).thenReturn(contact);

        // Действие
        ContactDTO result = contactService.save(contactDTO, 1L);

        // Проверка
        assertNotNull(result);
        assertEquals("Ivan", result.getName());
        verify(contactRepository, times(1)).save(any(Contact.class));
    }

    @Test
    void delete_ShouldDeleteContact_WhenUserIsOwner() {
        // Подготовка
        when(contactRepository.findById(10L)).thenReturn(Optional.of(contact));

        // Действие
        contactService.delete(10L, 1L);

        // Проверка
        verify(contactRepository, times(1)).delete(contact);
    }

    @Test
    void delete_ShouldThrowException_WhenUserIsNotOwner() {
        // Подготовка (ID владельца 1, удаляет пользователь 99)
        when(contactRepository.findById(10L)).thenReturn(Optional.of(contact));

        // Действие и проверка
        assertThrows(RuntimeException.class, () -> contactService.delete(10L, 99L));
    }
    @Test
    void update_ShouldUpdateContact_WhenUserIsOwner() {
        // Подготовка
        ContactDTO updateDto = new ContactDTO();
        updateDto.setName("New Name");

        when(contactRepository.findById(10L)).thenReturn(Optional.of(contact));
        when(contactRepository.save(any(Contact.class))).thenAnswer(i -> i.getArguments()[0]);

        // Действие
        ContactDTO result = contactService.update(10L, updateDto, 1L);

        // Проверка
        assertNotNull(result);
        assertEquals("New Name", result.getName());
        verify(contactRepository, times(1)).save(any(Contact.class));
    }

}
