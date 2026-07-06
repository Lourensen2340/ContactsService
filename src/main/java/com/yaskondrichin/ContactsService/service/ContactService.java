package com.yaskondrichin.ContactsService.service;

import com.yaskondrichin.ContactsService.DTO.ContactDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface ContactService {
    List<ContactDTO> findAll();
    List<ContactDTO> findAllByUserId(UUID userId);
    ContactDTO create(ContactDTO dto, UUID userId);
    ContactDTO update(UUID contactId, ContactDTO contactDTO, UUID userId);

    //void deleteContact(Long contactId);

    @Transactional
    void deleteContact(UUID contactId, UUID userId);
}