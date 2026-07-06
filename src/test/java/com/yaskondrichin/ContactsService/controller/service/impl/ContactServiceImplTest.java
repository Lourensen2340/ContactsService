package com.yaskondrichin.ContactsService.controller.service.impl;

import com.yaskondrichin.ContactsService.DTO.ContactDTO;
import com.yaskondrichin.ContactsService.Mapper.ContactMapper;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContactServiceImplTest {

    @Mock
    private ContactRepository contactRepository;

    @Mock
    private LoginRepository loginRepository;

    @Mock
    private ContactMapper contactMapper;

    @InjectMocks
    private ContactServiceImpl contactService;

    private Contact contact;
    private Login loginOwner;
    private ContactDTO contactDTO;
    private UUID userId;
    private UUID contactId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        contactId = UUID.randomUUID();

        loginOwner = new Login();
        loginOwner.setId(userId);
        loginOwner.setContacts(new ArrayList<>());

        contact = new Contact();
        contact.setId(contactId);
        contact.setName("Ivan");
        contact.setSurname("Ivanov");
        contact.setPhone("+375291234567");
        contact.setEmail("ivan@example.com");
        contact.setLogin(loginOwner); // Владельцем теперь является Login
        contact.setDeleted(false);

        loginOwner.getContacts().add(contact);

        contactDTO = new ContactDTO();
        contactDTO.setId(contactId);
        contactDTO.setName("Ivan");
        contactDTO.setSurname("Ivanov");
        contactDTO.setPhone("+375291234567");
        contactDTO.setEmail("ivan@example.com");
    }

    @Test
    void findAllByUserId_ShouldReturnMappedContactDTOs() {
        // Вызываем актуальный метод репозитория findAllByLoginId
        when(contactRepository.findAllByLoginId(userId)).thenReturn(List.of(contact));
        when(contactMapper.toDtoList(anyList())).thenReturn(List.of(contactDTO));

        List<ContactDTO> result = contactService.findAllByUserId(userId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Ivan", result.get(0).getName());
        verify(contactRepository, times(1)).findAllByLoginId(userId);
        verify(contactMapper, times(1)).toDtoList(anyList());
    }

    @Test
    void deleteContact_ShouldSetDeletedTrue_WhenUserIsOwner() {
        when(contactRepository.findById(contactId)).thenReturn(Optional.of(contact));

        contactService.deleteContact(contactId, userId);

        assertTrue(contact.isDeleted(), "Флаг deleted должен быть равен true после мягкого удаления");
        verify(contactRepository, times(1)).save(contact);
    }

    @Test
    void deleteContact_ShouldThrowException_WhenUserIsNotOwner() {
        UUID strangerId = UUID.randomUUID();
        when(contactRepository.findById(contactId)).thenReturn(Optional.of(contact));

        assertThrows(RuntimeException.class, () -> contactService.deleteContact(contactId, strangerId));
        verify(contactRepository, never()).save(any(Contact.class));
    }

    @Test
    void deleteContact_ShouldThrowException_WhenContactNotFound() {
        when(contactRepository.findById(contactId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> contactService.deleteContact(contactId, userId));
    }
}