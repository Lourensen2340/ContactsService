package com.yaskondrichin.ContactsService.controller.service.impl;

import com.yaskondrichin.ContactsService.DTO.ContactDTO;
import com.yaskondrichin.ContactsService.Mapper.ContactMapper;
import com.yaskondrichin.ContactsService.domain.model.Contact;
import com.yaskondrichin.ContactsService.domain.model.Login;
import com.yaskondrichin.ContactsService.domain.repo.ContactRepository;
import com.yaskondrichin.ContactsService.domain.repo.LoginRepository;
import com.yaskondrichin.ContactsService.service.impl.ContactServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
        contact.setLogin(loginOwner);
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
    @DisplayName("findAllByUserId: должен вернуть список DTO контактов пользователя")
    void findAllByUserId_ShouldReturnMappedContactDTOs() {
        // Given
        when(contactRepository.findAllByLoginId(userId)).thenReturn(List.of(contact));
        when(contactMapper.toDtoList(anyList())).thenReturn(List.of(contactDTO));

        // When
        List<ContactDTO> result = contactService.findAllByUserId(userId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Ivan", result.get(0).getName());
        verify(contactRepository, times(1)).findAllByLoginId(userId);
        verify(contactMapper, times(1)).toDtoList(anyList());
    }

    @Test
    @DisplayName("create: должен успешно создать и связать контакт с пользователем")
    void create_ShouldSaveAndReturnContactDTO_WhenUserExists() {
        // Given
        ContactDTO inputDto = new ContactDTO();
        inputDto.setName("NewName");

        Contact unsavedContact = new Contact();
        unsavedContact.setName("NewName");

        Contact savedContact = new Contact();
        savedContact.setId(UUID.randomUUID());
        savedContact.setName("NewName");
        savedContact.setLogin(loginOwner);

        ContactDTO resultDto = new ContactDTO();
        resultDto.setId(savedContact.getId());
        resultDto.setName("NewName");

        when(loginRepository.findById(userId)).thenReturn(Optional.of(loginOwner));
        when(contactMapper.toEntity(inputDto)).thenReturn(unsavedContact);
        when(contactRepository.save(unsavedContact)).thenReturn(savedContact);
        when(contactMapper.toDTO(savedContact)).thenReturn(resultDto);

        // When
        ContactDTO actualResult = contactService.create(inputDto, userId);

        // Then
        assertNotNull(actualResult);
        assertEquals(savedContact.getId(), actualResult.getId());
        assertEquals("NewName", actualResult.getName());
        assertEquals(loginOwner, unsavedContact.getLogin(), "Контакт должен быть привязан к найденному владельцу");

        verify(loginRepository, times(1)).findById(userId);
        verify(contactRepository, times(1)).save(unsavedContact);
    }

    @Test
    @DisplayName("create: должен выбросить исключение, если пользователь для привязки контакта не найден")
    void create_ShouldThrowException_WhenUserDoesNotExist() {
        // Given
        ContactDTO inputDto = new ContactDTO();
        when(loginRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                contactService.create(inputDto, userId)
        );

        assertTrue(exception.getMessage().contains("User not found with id: " + userId));
        verify(contactRepository, never()).save(any(Contact.class));
    }

    @Test
    @DisplayName("update: должен успешно обновить все поля контакта, если его обновляет владелец")
    void update_ShouldModifyFieldsAndSave_WhenUserIsOwner() {
        // Given
        ContactDTO updatedFieldsDto = new ContactDTO();
        updatedFieldsDto.setName("Petr");
        updatedFieldsDto.setSurname("Petrov");
        updatedFieldsDto.setPhone("+375297654321");
        updatedFieldsDto.setEmail("petr@example.com");

        Contact savedContact = new Contact();
        savedContact.setId(contactId);
        savedContact.setName("Petr");
        savedContact.setSurname("Petrov");
        savedContact.setPhone("+375297654321");
        savedContact.setEmail("petr@example.com");

        ContactDTO savedDto = new ContactDTO();
        savedDto.setId(contactId);
        savedDto.setName("Petr");

        when(contactRepository.findById(contactId)).thenReturn(Optional.of(contact));
        when(contactRepository.save(contact)).thenReturn(savedContact);
        when(contactMapper.toDTO(savedContact)).thenReturn(savedDto);

        // When
        ContactDTO result = contactService.update(contactId, updatedFieldsDto, userId);

        // Then
        assertNotNull(result);
        assertEquals("Petr", contact.getName());
        assertEquals("Petrov", contact.getSurname());
        assertEquals("+375297654321", contact.getPhone());
        assertEquals("petr@example.com", contact.getEmail());

        verify(contactRepository, times(1)).findById(contactId);
        verify(contactRepository, times(1)).save(contact);
    }

    @Test
    @DisplayName("update: должен выбросить исключение при попытке обновить чужой контакт")
    void update_ShouldThrowException_WhenUserIsNotOwner() {
        // Given
        UUID strangerId = UUID.randomUUID();
        ContactDTO updatedFieldsDto = new ContactDTO();

        when(contactRepository.findById(contactId)).thenReturn(Optional.of(contact));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                contactService.update(contactId, updatedFieldsDto, strangerId)
        );

        assertEquals("Доступ запрещен", exception.getMessage());
        verify(contactRepository, never()).save(any(Contact.class));
    }

    @Test
    @DisplayName("update: должен выбросить исключение, если обновляемый контакт не найден")
    void update_ShouldThrowException_WhenContactNotFound() {
        // Given
        ContactDTO updatedFieldsDto = new ContactDTO();
        when(contactRepository.findById(contactId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                contactService.update(contactId, updatedFieldsDto, userId)
        );

        assertEquals("Контакт не найден", exception.getMessage());
        verify(contactRepository, never()).save(any(Contact.class));
    }

    @Test
    @DisplayName("deleteContact: должен пометить контакт как удаленный (мягкое удаление), если это делает владелец")
    void deleteContact_ShouldSetDeletedTrue_WhenUserIsOwner() {
        // Given
        when(contactRepository.findById(contactId)).thenReturn(Optional.of(contact));

        // When
        contactService.deleteContact(contactId, userId);

        // Then
        assertTrue(contact.isDeleted(), "Флаг deleted должен быть равен true после мягкого удаления");
        verify(contactRepository, times(1)).save(contact);
    }

    @Test
    @DisplayName("deleteContact: должен выбросить исключение при попытке удалить чужой контакт")
    void deleteContact_ShouldThrowException_WhenUserIsNotOwner() {
        // Given
        UUID strangerId = UUID.randomUUID();
        when(contactRepository.findById(contactId)).thenReturn(Optional.of(contact));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                contactService.deleteContact(contactId, strangerId)
        );

        assertEquals("You do not have permission", exception.getMessage());
        verify(contactRepository, never()).save(any(Contact.class));
    }

    @Test
    @DisplayName("deleteContact: должен выбросить исключение, если удаляемый контакт не найден")
    void deleteContact_ShouldThrowException_WhenContactNotFound() {
        // Given
        when(contactRepository.findById(contactId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                contactService.deleteContact(contactId, userId)
        );

        assertEquals("Contact not found", exception.getMessage());
        verify(contactRepository, never()).save(any(Contact.class));
    }

    @Test
    @DisplayName("findAll: должен вернуть полный список DTO всех существующих контактов")
    void findAll_ShouldReturnAllContactsMappedToDTO() {
        // Given
        Contact secondContact = new Contact();
        secondContact.setId(UUID.randomUUID());
        secondContact.setName("Anna");

        ContactDTO secondContactDto = new ContactDTO();
        secondContactDto.setId(secondContact.getId());
        secondContactDto.setName("Anna");

        when(contactRepository.findAll()).thenReturn(List.of(contact, secondContact));
        when(contactMapper.toDTO(contact)).thenReturn(contactDTO);
        when(contactMapper.toDTO(secondContact)).thenReturn(secondContactDto);

        // When
        List<ContactDTO> result = contactService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Ivan", result.get(0).getName());
        assertEquals("Anna", result.get(1).getName());

        verify(contactRepository, times(1)).findAll();
        verify(contactMapper, times(2)).toDTO(any(Contact.class));
    }
}