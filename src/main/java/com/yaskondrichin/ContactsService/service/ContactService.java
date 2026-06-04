package com.yaskondrichin.ContactsService.service;

import com.yaskondrichin.ContactsService.DTO.ContactDTO;

import java.util.List;

public interface ContactService {
    List<ContactDTO> findAll();
    List<ContactDTO> findAllByUserId(Long userId);
    ContactDTO save(ContactDTO contactDTO, Long userId);
    ContactDTO update(Long id, ContactDTO contactDTO, Long userId);
    void delete(Long id, Long userId);
}