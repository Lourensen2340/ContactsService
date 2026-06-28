package com.yaskondrichin.ContactsService.controller.service;

import com.yaskondrichin.ContactsService.DTO.ContactDTO;
import com.yaskondrichin.ContactsService.Mapper.ContactMapper;
import com.yaskondrichin.ContactsService.domain.model.Contact;
import com.yaskondrichin.ContactsService.domain.model.Login;
import com.yaskondrichin.ContactsService.domain.model.User;
import com.yaskondrichin.ContactsService.domain.repo.ContactRepository;
import com.yaskondrichin.ContactsService.domain.repo.LoginRepository;
import com.yaskondrichin.ContactsService.domain.repo.UserRepository;
import com.yaskondrichin.ContactsService.service.impl.ContactServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContactServiceTest {

    @Mock
    private ContactRepository contactRepository;

    @Mock
    private LoginRepository loginRepository;

    @Mock
    private ContactMapper contactMapper;

    @Mock
    private UserRepository userRepository;

    // Внедряем заглушки в реализацию, но тестируем поведение через интерфейс ContactService
    @InjectMocks
    private ContactServiceImpl contactService;

    // --- Тесты для findAll() ---
    @Test
    void findAll_ShouldReturnEmptyList() {
        List<ContactDTO> result = contactService.findAll();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // --- Тесты для findAllByUserId() ---
    @Test
    void findAllByUserId_ShouldReturnMappedDtoList() {
        Long userId = 1L;
        List<Contact> mockContacts = List.of(new Contact(), new Contact());
        List<ContactDTO> mockDtos = List.of(new ContactDTO(), new ContactDTO());

        when(contactRepository.findAllByUserIdAndIsDeletedFalse(userId)).thenReturn(mockContacts);
        when(contactMapper.toDtoList(mockContacts)).thenReturn(mockDtos);

        List<ContactDTO> result = contactService.findAllByUserId(userId);

        assertEquals(2, result.size());
        verify(contactRepository, times(1)).findAllByUserIdAndIsDeletedFalse(userId);
        verify(contactMapper, times(1)).toDtoList(mockContacts);
    }

    // --- Тесты для create() ---
    @Test
    void create_ShouldSaveAndReturnContact_WhenUserAndLoginExist() {
        // Given
        Long userId = 1L;
        ContactDTO inputDto = new ContactDTO();
        inputDto.setName("Иван");

        Login mockOwner = new Login();
        mockOwner.setId(userId);
        mockOwner.setContacts(new ArrayList<>());

        User mockUser = new User();
        mockUser.setId(userId);

        Contact mockContact = new Contact();
        mockContact.setUsers(new ArrayList<>());

        Contact savedContact = new Contact();
        ContactDTO expectedDto = new ContactDTO();
        expectedDto.setId(55L);
        expectedDto.setName("Иван");

        when(loginRepository.findById(userId)).thenReturn(Optional.of(mockOwner));
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(contactMapper.toEntity(inputDto)).thenReturn(mockContact);
        when(contactRepository.save(any(Contact.class))).thenReturn(savedContact);
        when(contactMapper.toDTO(savedContact)).thenReturn(expectedDto);

        // When
        ContactDTO result = contactService.create(inputDto, userId);

        // Then
        assertNotNull(result);
        assertEquals(55L, result.getId());
        verify(contactRepository, times(1)).save(mockContact);
        assertTrue(mockOwner.getContacts().contains(mockContact));
    }

    @Test
    void create_ShouldThrowException_WhenLoginNotFound() {
        Long userId = 1L;
        ContactDTO dto = new ContactDTO();
        when(loginRepository.findById(userId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> contactService.create(dto, userId));
        assertTrue(exception.getMessage().contains("User not found with id"));
        verify(contactRepository, never()).save(any());
    }

    @Test
    void create_ShouldThrowException_OnDataIntegrityViolation() {
        Long userId = 1L;
        ContactDTO dto = new ContactDTO();

        when(loginRepository.findById(userId)).thenReturn(Optional.of(new Login()));
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(contactMapper.toEntity(dto)).thenReturn(new Contact());
        when(contactRepository.save(any(Contact.class))).thenThrow(new DataIntegrityViolationException("Duplicate"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> contactService.create(dto, userId));
        assertEquals("Контакт с таким телефоном или email уже существует в вашей записной книжке", exception.getMessage());
    }

    // --- Тесты для update() ---
    @Test
    void update_ShouldUpdateAndReturnContact_WhenUserIsOwner() {
        // Given
        Long contactId = 10L;
        Long userId = 1L;

        ContactDTO updateFields = new ContactDTO();
        updateFields.setName("Алексей");
        updateFields.setSurname("Петров");
        updateFields.setPhone("+375290000000");
        updateFields.setEmail("alex@mail.com");

        User ownerUser = new User();
        ownerUser.setId(userId);

        Contact existingContact = new Contact();
        existingContact.setId(contactId);
        existingContact.setUser(ownerUser); // Владелец совпадает с userId

        Contact savedContact = new Contact();
        ContactDTO expectedDto = new ContactDTO();
        expectedDto.setName("Алексей");

        when(contactRepository.findById(contactId)).thenReturn(Optional.of(existingContact));
        when(contactRepository.save(existingContact)).thenReturn(savedContact);
        when(contactMapper.toDTO(savedContact)).thenReturn(expectedDto);

        // When
        ContactDTO result = contactService.update(contactId, updateFields, userId);

        // Then
        assertNotNull(result);
        assertEquals("Алексей", existingContact.getName());
        assertEquals("Петров", existingContact.getSurname());
        verify(contactRepository, times(1)).save(existingContact);
    }

    @Test
    void update_ShouldThrowException_WhenContactNotFound() {
        Long contactId = 99L;
        Long userId = 1L;
        when(contactRepository.findById(contactId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                contactService.update(contactId, new ContactDTO(), userId));
        assertEquals("Контакт не найден", exception.getMessage());
    }

    @Test
    void update_ShouldThrowException_WhenUserIsNotOwner() {
        Long contactId = 10L;
        Long currentUserId = 1L; // Тот, кто пытается обновить
        Long realOwnerId = 2L;   // Настоящий владелец

        User realOwner = new User();
        realOwner.setId(realOwnerId);

        Contact contact = new Contact();
        contact.setUser(realOwner);

        when(contactRepository.findById(contactId)).thenReturn(Optional.of(contact));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                contactService.update(contactId, new ContactDTO(), currentUserId));
        assertEquals("Доступ запрещен", exception.getMessage());
        verify(contactRepository, never()).save(any());
    }

    // --- Тесты для deleteContact() ---
    @Test
    void deleteContact_ShouldSoftDelete_WhenContactInUserList() {
        // Given
        Long contactId = 10L;
        Long userId = 1L;

        Contact contactToDelete = new Contact();
        contactToDelete.setId(contactId);
        contactToDelete.setDeleted(false);

        Login loginUser = new Login();
        loginUser.setId(userId);
        List<Contact> userContacts = new ArrayList<>();
        userContacts.add(contactToDelete);
        loginUser.setContacts(userContacts); // Связываем контакт с пользователем

        when(loginRepository.findById(userId)).thenReturn(Optional.of(loginUser));
        when(contactRepository.findById(contactId)).thenReturn(Optional.of(contactToDelete));

        // When
        contactService.deleteContact(contactId, userId);

        // Then
        assertTrue(contactToDelete.isDeleted(), "Флаг мягкого удаления должен быть true");
        assertFalse(loginUser.getContacts().contains(contactToDelete), "Контакт должен быть удален из коллекции пользователя");
        verify(contactRepository, times(1)).save(contactToDelete);
        verify(loginRepository, times(1)).save(loginUser);
    }

    @Test
    void deleteContact_ShouldThrowException_WhenContactNotInUserList() {
        Long contactId = 10L;
        Long userId = 1L;

        Contact strangerContact = new Contact();
        strangerContact.setId(contactId);

        Login loginUser = new Login();
        loginUser.setId(userId);
        loginUser.setContacts(new ArrayList<>()); // У пользователя пустой список контактов

        when(loginRepository.findById(userId)).thenReturn(Optional.of(loginUser));
        when(contactRepository.findById(contactId)).thenReturn(Optional.of(strangerContact));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                contactService.deleteContact(contactId, userId));
        assertEquals("You do not have permission to delete this contact or it's not in your list", exception.getMessage());

        verify(contactRepository, never()).save(any());
        verify(loginRepository, never()).save(loginUser);
    }
}
