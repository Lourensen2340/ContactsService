package com.yaskondrichin.ContactsService.controller.service;

import com.yaskondrichin.ContactsService.DTO.ContactDTO;
import com.yaskondrichin.ContactsService.Mapper.ContactMapper;
import com.yaskondrichin.ContactsService.domain.model.Contact;
import com.yaskondrichin.ContactsService.domain.model.Login;
import com.yaskondrichin.ContactsService.domain.repo.ContactRepository;
import com.yaskondrichin.ContactsService.domain.repo.LoginRepository;
import com.yaskondrichin.ContactsService.service.impl.ContactServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    @InjectMocks
    private ContactServiceImpl contactService;

    @Test
    void findAll_ShouldReturnEmptyList() {
        List<ContactDTO> result = contactService.findAll();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findAllByUserId_ShouldReturnMappedDtoList() {
        UUID userId = UUID.randomUUID();
        List<Contact> mockContacts = List.of(new Contact(), new Contact());
        List<ContactDTO> mockDtos = List.of(new ContactDTO(), new ContactDTO());

        when(contactRepository.findAllByLoginId(userId)).thenReturn(mockContacts);
        when(contactMapper.toDtoList(mockContacts)).thenReturn(mockDtos);

        List<ContactDTO> result = contactService.findAllByUserId(userId);

        assertEquals(2, result.size());
        verify(contactRepository, times(1)).findAllByLoginId(userId);
        verify(contactMapper, times(1)).toDtoList(mockContacts);
    }

    @Test
    void create_ShouldSaveAndReturnContact_WhenLoginExists() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID generatedContactId = UUID.randomUUID();
        ContactDTO inputDto = new ContactDTO();
        inputDto.setName("Иван");

        Login mockOwner = new Login();
        mockOwner.setId(userId);
        mockOwner.setContacts(new ArrayList<>());

        Contact mockContact = new Contact();

        Contact savedContact = new Contact();
        ContactDTO expectedDto = new ContactDTO();
        expectedDto.setId(generatedContactId);
        expectedDto.setName("Иван");

        when(loginRepository.findById(userId)).thenReturn(Optional.of(mockOwner));
        when(contactMapper.toEntity(inputDto)).thenReturn(mockContact);
        when(contactRepository.save(any(Contact.class))).thenReturn(savedContact);
        when(contactMapper.toDTO(savedContact)).thenReturn(expectedDto);

        // When
        ContactDTO result = contactService.create(inputDto, userId);

        // Then
        assertNotNull(result);
        assertEquals(generatedContactId, result.getId());
        verify(contactRepository, times(1)).save(mockContact);
    }

    @Test
    void create_ShouldThrowException_WhenLoginNotFound() {
        UUID userId = UUID.randomUUID();
        ContactDTO dto = new ContactDTO();
        when(loginRepository.findById(userId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> contactService.create(dto, userId));
        assertTrue(exception.getMessage().contains("User not found with id"));
        verify(contactRepository, never()).save(any());
    }

    @Test
    void create_ShouldThrowDataIntegrityViolationException_OnDuplicate() {
        UUID userId = UUID.randomUUID();
        ContactDTO dto = new ContactDTO();

        when(loginRepository.findById(userId)).thenReturn(Optional.of(new Login()));
        when(contactMapper.toEntity(dto)).thenReturn(new Contact());
        when(contactRepository.save(any(Contact.class))).thenThrow(new DataIntegrityViolationException("Duplicate"));

        // Ожидаем прямое выбрасывание DataIntegrityViolationException из репозитория
        assertThrows(DataIntegrityViolationException.class, () -> contactService.create(dto, userId));
    }

    @Test
    void update_ShouldUpdateAndReturnContact_WhenUserIsOwner() {
        // Given
        UUID contactId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        ContactDTO updateFields = new ContactDTO();
        updateFields.setName("Алексей");
        updateFields.setSurname("Петров");
        updateFields.setPhone("+375290000000");
        updateFields.setEmail("alex@mail.com");

        Login ownerLogin = new Login();
        ownerLogin.setId(userId);

        Contact existingContact = new Contact();
        existingContact.setId(contactId);
        existingContact.setLogin(ownerLogin);

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
        UUID contactId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        when(contactRepository.findById(contactId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                contactService.update(contactId, new ContactDTO(), userId));
        assertEquals("Контакт не найден", exception.getMessage());
    }

    @Test
    void update_ShouldThrowException_WhenUserIsNotOwner() {
        UUID contactId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        UUID realOwnerId = UUID.randomUUID();

        Login realOwner = new Login();
        realOwner.setId(realOwnerId);

        Contact contact = new Contact();
        contact.setLogin(realOwner);

        when(contactRepository.findById(contactId)).thenReturn(Optional.of(contact));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                contactService.update(contactId, new ContactDTO(), currentUserId));
        assertEquals("Доступ запрещен", exception.getMessage());
        verify(contactRepository, never()).save(any());
    }

    @Test
    void deleteContact_ShouldSoftDelete_WhenUserIsOwner() {
        // Given
        UUID contactId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Login loginUser = new Login();
        loginUser.setId(userId);

        Contact contactToDelete = new Contact();
        contactToDelete.setId(contactId);
        contactToDelete.setLogin(loginUser);
        contactToDelete.setDeleted(false);

        when(contactRepository.findById(contactId)).thenReturn(Optional.of(contactToDelete));

        // When
        contactService.deleteContact(contactId, userId);

        // Then
        assertTrue(contactToDelete.isDeleted(), "Флаг мягкого удаления должен быть true");
        verify(contactRepository, times(1)).save(contactToDelete);
    }
}