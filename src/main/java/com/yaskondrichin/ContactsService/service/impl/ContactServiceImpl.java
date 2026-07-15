package com.yaskondrichin.ContactsService.service.impl;

import com.yaskondrichin.ContactsService.DTO.ContactDTO;
import com.yaskondrichin.ContactsService.Mapper.ContactMapper;
import com.yaskondrichin.ContactsService.domain.model.Contact;
import com.yaskondrichin.ContactsService.domain.model.Login;
import com.yaskondrichin.ContactsService.domain.repo.ContactRepository;
import com.yaskondrichin.ContactsService.domain.repo.LoginRepository;
import com.yaskondrichin.ContactsService.service.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID; // ИСПРАВЛЕНО

@Service
@RequiredArgsConstructor
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;
    private final LoginRepository loginRepository;
    private final ContactMapper contactMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ContactDTO> findAllByUserId(UUID userId) { // ИСПРАВЛЕНО: UUID
        List<Contact> contacts = contactRepository.findAllByLoginId(userId);
        return contactMapper.toDtoList(contacts);
    }

    @Override
    @Transactional
    public ContactDTO create(ContactDTO dto, UUID userId) { // ИСПРАВЛЕНО: UUID
        Login owner = loginRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Contact contact = contactMapper.toEntity(dto);
        contact.setLogin(owner);

        Contact savedContact = contactRepository.save(contact);
        return contactMapper.toDTO(savedContact);
    }

    @Override
    @Transactional
    public ContactDTO update(UUID contactId, ContactDTO contactDTO, UUID userId) { // ИСПРАВЛЕНО: UUID
        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new RuntimeException("Контакт не найден"));

        if (!contact.getLogin().getId().equals(userId)) {
            throw new RuntimeException("Доступ запрещен");
        }

        contact.setName(contactDTO.getName());
        contact.setSurname(contactDTO.getSurname());
        contact.setPhone(contactDTO.getPhone());
        contact.setEmail(contactDTO.getEmail());

        return contactMapper.toDTO(contactRepository.save(contact));
    }

    @Override
    @Transactional
    public void deleteContact(UUID contactId, UUID userId) { // ИСПРАВЛЕНО: UUID
        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new RuntimeException("Contact not found"));

        if (!contact.getLogin().getId().equals(userId)) {
            throw new RuntimeException("You do not have permission");
        }

        contact.setDeleted(true);
        contactRepository.save(contact);
    }

    @Override
    public List<ContactDTO> findAll() {
        return contactRepository.findAll().stream().map(contactMapper::toDTO).toList();
    }
}