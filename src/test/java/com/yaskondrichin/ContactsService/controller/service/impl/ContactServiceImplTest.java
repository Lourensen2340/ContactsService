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

import java.util.List;
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
        contact.setSurname("Ivanov");
        contact.setPhone("+123456789");
        contact.setEmail("ivan@example.com");
        contact.setUser(owner);

        contactDTO = new ContactDTO();
        contactDTO.setName("Ivan");
        contactDTO.setSurname("Ivanov");
        contactDTO.setPhone("+123456789");
        contactDTO.setEmail("ivan@example.com");
    }

    // --- ТЕСТЫ МЕТОДА findAllByUserId ---

    @Test
    void findAllByUserId_ShouldReturnMappedContactDTOs() {
        // Подготовка
        when(contactRepository.findAllByUserId(1L)).thenReturn(List.of(contact));

        // Действие
        List<ContactDTO> result = contactService.findAllByUserId(1L);

        // Проверка
        assertNotNull(result);
        assertEquals(1, result.size());
        ContactDTO mappedDto = result.get(0);
        assertEquals(contact.getId(), mappedDto.getId());
        assertEquals(contact.getName(), mappedDto.getName());
        assertEquals(contact.getSurname(), mappedDto.getSurname());
        assertEquals(contact.getPhone(), mappedDto.getPhone());
        assertEquals(contact.getEmail(), mappedDto.getEmail());
        verify(contactRepository, times(1)).findAllByUserId(1L);
    }

    // --- ТЕСТЫ МЕТОДА save ---

    @Test
    void save_ShouldCreateNewContact_WhenIdIsNull() {
        // Подготовка
        contactDTO.setId(null);
        when(loginRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(contactRepository.save(any(Contact.class))).thenReturn(contact);

        // Действие
        ContactDTO result = contactService.save(contactDTO, 1L);

        // Проверка
        assertNotNull(result);
        assertEquals("Ivan", result.getName());
        assertEquals("Ivanov", result.getSurname());
        assertEquals("ivan@example.com", result.getEmail());
        verify(loginRepository, times(1)).findById(1L);
        verify(contactRepository, times(1)).save(any(Contact.class));
    }

    @Test
    void save_ShouldThrowException_WhenUserNotFoundForNewContact() {
        // Подготовка
        contactDTO.setId(null);
        when(loginRepository.findById(1L)).thenReturn(Optional.empty());

        // Действие и проверка
        assertThrows(RuntimeException.class, () -> contactService.save(contactDTO, 1L));
        verify(contactRepository, never()).save(any(Contact.class));
    }

    // --- ТЕСТЫ МЕТОДА update ---

    @Test
    void update_ShouldUpdateContact_WhenUserIsOwner() {
        // Подготовка
        ContactDTO updateDto = new ContactDTO();
        updateDto.setName("New Name");
        updateDto.setSurname("New Surname");
        updateDto.setPhone("+987654321");
        updateDto.setEmail("new@example.com");

        when(contactRepository.findById(10L)).thenReturn(Optional.of(contact));
        when(contactRepository.save(any(Contact.class))).thenAnswer(invocation -> invocation.getArguments()[0]);

        // Действие
        ContactDTO result = contactService.update(10L, updateDto, 1L);

        // Проверка
        assertNotNull(result);
        assertEquals("New Name", result.getName());
        assertEquals("New Surname", result.getSurname());
        assertEquals("+987654321", result.getPhone());
        assertEquals("new@example.com", result.getEmail());
        verify(contactRepository, times(1)).save(any(Contact.class));
    }

    @Test
    void update_ShouldThrowException_WhenContactNotFound() {
        // Подготовка
        when(contactRepository.findById(10L)).thenReturn(Optional.empty());

        // Действие и проверка
        assertThrows(RuntimeException.class, () -> contactService.update(10L, contactDTO, 1L));
        verify(contactRepository, never()).save(any(Contact.class));
    }

    @Test
    void update_ShouldThrowException_WhenUserIsNotOwner() {
        // Подготовка
        when(contactRepository.findById(10L)).thenReturn(Optional.of(contact));

        // Действие и проверка (ID владельца 1L, пытается обновить 99L)
        assertThrows(RuntimeException.class, () -> contactService.update(10L, contactDTO, 99L));
        verify(contactRepository, never()).save(any(Contact.class));
    }

    // --- ТЕСТЫ МЕТОДА delete ---

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
        verify(contactRepository, never()).delete(any(Contact.class));
    }

    @Test
    void delete_ShouldThrowException_WhenContactNotFound() {
        // Подготовка
        when(contactRepository.findById(10L)).thenReturn(Optional.empty());

        // Действие и проверка
        assertThrows(RuntimeException.class, () -> contactService.delete(10L, 1L));
        verify(contactRepository, never()).delete(any(Contact.class));
    }
}