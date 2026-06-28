package com.yaskondrichin.ContactsService.service;

import com.yaskondrichin.ContactsService.DTO.ContactDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface ContactService {
    List<ContactDTO> findAll();
    List<ContactDTO> findAllByUserId(Long userId);
    ContactDTO create(ContactDTO dto, Long userId);
    ContactDTO update(Long contactId, ContactDTO contactDTO, Long userId);

    //void deleteContact(Long contactId);

    @Transactional
    void deleteContact(Long contactId, Long userId);
}