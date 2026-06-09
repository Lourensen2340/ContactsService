package com.yaskondrichin.ContactsService.service.impl;

import com.yaskondrichin.ContactsService.DTO.ContactDTO;
import com.yaskondrichin.ContactsService.domain.model.Contact;
import com.yaskondrichin.ContactsService.domain.model.Login;
import com.yaskondrichin.ContactsService.domain.repo.ContactRepository;
import com.yaskondrichin.ContactsService.domain.repo.LoginRepository;
import com.yaskondrichin.ContactsService.exception.ResourceNotFoundException;
import com.yaskondrichin.ContactsService.service.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;
    private final LoginRepository loginRepository;

    @Override
    public List<ContactDTO> findAll() {
        return List.of();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContactDTO> findAllByUserId(Long userId) {
        return contactRepository.findAllByUserId(userId).stream()
                .map(contact -> {
                    ContactDTO dto = new ContactDTO();
                    dto.setId(contact.getId());
                    dto.setName(contact.getName());
                    dto.setSurname(contact.getSurname());
                    dto.setPhone(contact.getPhone());
                    dto.setEmail(contact.getEmail());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ContactDTO save(ContactDTO dto, Long userId) {
        Contact contact;

        // Исправляем проверку: если id равен null или 0, то это ТОЧНО новый контакт
        if (dto.getId() != null && dto.getId() != 0) {
            // Логика обновления существующего контакта
            contact = contactRepository.findById(dto.getId())
                    .orElseThrow(() -> new RuntimeException("Contact not found with id: " + dto.getId()));
        } else {
            // Логика создания нового контакта
            contact = new Contact();

            // Привязываем владельца (пользователя)
            Login owner = loginRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
            contact.setUser(owner);
        }

        // КРИТИЧЕСКИЙ ШАГ: Переносим ВСЕ данные из dto в сущность перед сохранением!
        contact.setName(dto.getName());
        contact.setSurname(dto.getSurname()); // Теперь фамилия не будет null
        contact.setPhone(dto.getPhone());
        contact.setEmail(dto.getEmail());     // Переносим email

        // Сохраняем заполненную сущность в БД
        Contact savedContact = contactRepository.save(contact);

        // Собираем ответный DTO, заполняя его реальными сохраненными данными
        ContactDTO responseDto = new ContactDTO();
        responseDto.setId(savedContact.getId());
        responseDto.setName(savedContact.getName());
        responseDto.setSurname(savedContact.getSurname());
        responseDto.setPhone(savedContact.getPhone());
        responseDto.setEmail(savedContact.getEmail());

        return responseDto;
    }

    @Override // Эта аннотация обязательна, она подтверждает реализацию метода из интерфейса
    @Transactional
    public ContactDTO update(Long contactId, ContactDTO dto, Long userId) {
        // 1. Ищем редактируемый контакт в базе данных
        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new RuntimeException("Contact not found with id: " + contactId));

        // 2. Проверяем права: совпадает ли владелец контакта с userId из токена
        if (!contact.getUser().getId().equals(userId)) {
            throw new RuntimeException("You do not have permission to modify this contact");
        }

        // 3. Обновляем поля сущности новыми данными из DTO
        contact.setName(dto.getName());
        contact.setSurname(dto.getSurname());
        contact.setPhone(dto.getPhone());
        contact.setEmail(dto.getEmail());

        // 4. Сохраняем измененную сущность в БД
        Contact savedContact = contactRepository.save(contact);

        // 5. Ручной маппинг: собираем ответный DTO (вместо отсутствующего метода toDTO)
        ContactDTO responseDto = new ContactDTO();
        responseDto.setId(savedContact.getId());
        responseDto.setName(savedContact.getName());
        responseDto.setSurname(savedContact.getSurname());
        responseDto.setPhone(savedContact.getPhone());
        responseDto.setEmail(savedContact.getEmail());

        return responseDto;
    }

    @Override
    @Transactional
    public void delete(Long contactId, Long userId) {
        // 1. Находим контакт в базе данных
        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new RuntimeException("Contact not found with id: " + contactId));

        // 2. Логируем для отладки, чтобы в консоли точно видеть, что с чем сравнивается
        System.out.println("Владелец контакта в БД (ID): " + contact.getUser().getId());
        System.out.println("Пользователь из токена (ID): " + userId);

        // 3. Надежная проверка прав через .equals()
        if (!contact.getUser().getId().equals(userId)) {
            throw new RuntimeException("You do not have permission to delete this contact");
        }

        // 4. Если ID совпали — удаляем контакт
        contactRepository.delete(contact);
    }
}