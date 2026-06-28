package com.yaskondrichin.ContactsService.controller.service.impl;

import com.yaskondrichin.ContactsService.DTO.ContactDTO;
import com.yaskondrichin.ContactsService.Mapper.ContactMapper;
import com.yaskondrichin.ContactsService.domain.model.Contact;
import com.yaskondrichin.ContactsService.domain.model.Login;
import com.yaskondrichin.ContactsService.domain.model.User;
import com.yaskondrichin.ContactsService.domain.repo.ContactRepository;
import com.yaskondrichin.ContactsService.domain.repo.LoginRepository;
import com.yaskondrichin.ContactsService.domain.repo.UserRepository;
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

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ContactServiceImpl contactService;

    private Contact contact;
    private Login loginOwner;
    private User userOwner;
    private ContactDTO contactDTO;

    @BeforeEach
    void setUp() {
        // Создаем объект Login для симуляции авторизованного аккаунта
        loginOwner = new Login();
        loginOwner.setId(1L);
        loginOwner.setContacts(new ArrayList<>()); // Инициализируем список для проверки прав

        // ИСПРАВЛЕНИЕ: Создаем объект User, который ожидает сущность Contact
        userOwner = new User();
        userOwner.setId(1L);
        userOwner.setUsername("Ivan");

        contact = new Contact();
        contact.setId(10L);
        contact.setName("Ivan");
        contact.setSurname("Ivanov");
        contact.setPhone("+375291234567");
        contact.setEmail("ivan@example.com");
        contact.setUser(userOwner); // ТЕПЕРЬ ТИПЫ СОВПАДАЮТ: Передаем User вместо Login
        contact.setDeleted(false);

        // Связываем контакт со списком аккаунта Login, так как сервис проверяет владельца по этой коллекции
        loginOwner.getContacts().add(contact);

        contactDTO = new ContactDTO();
        contactDTO.setId(10L);
        contactDTO.setName("Ivan");
        contactDTO.setSurname("Ivanov");
        contactDTO.setPhone("+375291234567");
        contactDTO.setEmail("ivan@example.com");
    }

    // --- ТЕСТЫ МЕТОДА findAllByUserId ---

    @Test
    void findAllByUserId_ShouldReturnMappedContactDTOs() {
        when(contactRepository.findAllByUserIdAndIsDeletedFalse(1L)).thenReturn(List.of(contact));
        when(contactMapper.toDtoList(anyList())).thenReturn(List.of(contactDTO));

        List<ContactDTO> result = contactService.findAllByUserId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Ivan", result.get(0).getName());
        verify(contactRepository, times(1)).findAllByUserIdAndIsDeletedFalse(1L);
        verify(contactMapper, times(1)).toDtoList(anyList());
    }

    // --- ТЕСТЫ МЕТОДА deleteContact (МЯГКОЕ УДАЛЕНИЕ) ---

    @Test
    void deleteContact_ShouldSetDeletedTrue_WhenUserIsOwner() {
        when(loginRepository.findById(1L)).thenReturn(Optional.of(loginOwner));
        when(contactRepository.findById(10L)).thenReturn(Optional.of(contact));

        contactService.deleteContact(10L, 1L);

        assertTrue(contact.isDeleted(), "Флаг deleted должен быть равен true после мягкого удаления");
        verify(contactRepository, times(1)).save(contact);
    }

    @Test
    void deleteContact_ShouldThrowException_WhenUserIsNotOwner() {
        Login stranger = new Login();
        stranger.setId(99L);
        stranger.setContacts(new ArrayList<>()); // Пустой список контактов чужого пользователя

        when(loginRepository.findById(99L)).thenReturn(Optional.of(stranger));
        when(contactRepository.findById(10L)).thenReturn(Optional.of(contact));

        assertThrows(RuntimeException.class, () -> contactService.deleteContact(10L, 99L));
        verify(contactRepository, never()).save(any(Contact.class));
    }

    @Test
    void deleteContact_ShouldThrowException_WhenContactNotFound() {
        when(loginRepository.findById(1L)).thenReturn(Optional.of(loginOwner));
        when(contactRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> contactService.deleteContact(10L, 1L));
    }
}