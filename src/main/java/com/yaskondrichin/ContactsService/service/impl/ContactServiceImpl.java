package com.yaskondrichin.ContactsService.service.impl;

import com.yaskondrichin.ContactsService.DTO.ContactDTO;
import com.yaskondrichin.ContactsService.Mapper.ContactMapper;
import com.yaskondrichin.ContactsService.domain.model.Contact;
import com.yaskondrichin.ContactsService.domain.model.Login;
import com.yaskondrichin.ContactsService.domain.model.User;
import com.yaskondrichin.ContactsService.domain.repo.ContactRepository;
import com.yaskondrichin.ContactsService.domain.repo.LoginRepository;
import com.yaskondrichin.ContactsService.domain.repo.UserRepository;
import com.yaskondrichin.ContactsService.service.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;
    private final LoginRepository loginRepository;
    private final ContactMapper contactMapper; // Внедряем исправленный маппер
    private final UserRepository userRepository;

    @Override
    public List<ContactDTO> findAll() {
        return List.of();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContactDTO> findAllByUserId(Long userId) {
        List<Contact> contacts = contactRepository.findAllByUserIdAndIsDeletedFalse(userId);
        // Вместо ручного stream().map() используем готовый метод маппера для списков
        return contactMapper.toDtoList(contacts);
    }

    // МЕТОД 1: ТОЛЬКО СОЗДАНИЕ (Принимает DTO без ID)
    public ContactDTO create(ContactDTO dto, Long userId) {
        try {
            // 1. Ищем владельца логина
            Login owner = loginRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

            // 2. Ищем связанного пользователя
            User currentUser = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден с id: " + userId));

            // 3. Преобразуем DTO в сущность
            Contact contact = contactMapper.toEntity(dto);

            // === ЖЕЛЕЗОБЕТОННАЯ ЗАЩИТА ОТ МАППЕРА ===
            if (contact.getUsers() == null) {
                contact.setUsers(new ArrayList<>());
            }
            if (owner.getContacts() == null) {
                owner.setContacts(new ArrayList<>());
            }
            // ========================================

            // 4. Устанавливаем связи
            contact.getUsers().add(owner);
            owner.getContacts().add(contact);
            contact.setUser(currentUser);

            // 5. Сохраняем в базу данных
            Contact savedContact = contactRepository.save(contact);
            return contactMapper.toDTO(savedContact);

        } catch (DataIntegrityViolationException e) {
            // Ошибка уникальности (дубликат телефона/email)
            throw new RuntimeException("Контакт с таким телефоном или email уже существует в вашей записной книжке");
        } catch (Exception e) {
            // === ЕСЛИ КОД УПАДЕТ, МЫ НАКОНЕЦ-ТО УВИДИМ ПОЧЕМУ ===
            System.err.println("ОШИБКА ПРИ СОЗДАНИИ КОНТАКТА: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Внутренняя ошибка сервера: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public ContactDTO update(Long contactId, ContactDTO contactDTO, Long userId) {
        System.out.println("\n=== [DEBUG START] МЕТОД UPDATE ===");
        System.out.println("Входной contactId: " + contactId);
        System.out.println("Входной userId (из токена): " + userId);

        // 1. Ищем контакт
        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> {
                    System.out.println("ОШИБКА: Контакт с id " + contactId + " не найден в БД!");
                    return new RuntimeException("Контакт не найден");
                });

        System.out.println("Контакт успешно найден в БД! Имя: " + contact.getName());

        // 2. Проверяем связь с пользователем на null
        if (contact.getUser() == null) {
            System.out.println("ОШИБКА: У контакта в базе данных поле user_id равен NULL!");
            throw new RuntimeException("У контакта нет владельца");
        }

        System.out.println("ID владельца из БД: " + contact.getUser().getId());

        // 3. Проверяем совпадение ID
        if (!contact.getUser().getId().equals(userId)) {
            System.out.println("ОШИБКА: ID владельца (" + contact.getUser().getId() +
                    ") не совпадает с userId из токена (" + userId + ")!");
            throw new RuntimeException("Доступ запрещен");
        }

        System.out.println("=== [DEBUG SUCCESS] Проверки пройдены успешно! ===\n");

        // Дальше ваш обычный код обновления...
        contact.setName(contactDTO.getName());
        contact.setSurname(contactDTO.getSurname());
        contact.setPhone(contactDTO.getPhone());
        contact.setEmail(contactDTO.getEmail());

        Contact updatedContact = contactRepository.save(contact);
        return contactMapper.toDTO(updatedContact);
    }

    @Transactional
    @Override
    public void deleteContact(Long contactId, Long userId) { // Имя и типы строго как в интерфейсе!
        // 1. Находим пользователя
        Login loginUser = loginRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // 2. Находим контакт (ИСПРАВЛЕНО: ищем по contactId, а не по userId!)
        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new RuntimeException("Contact not found with id: " + contactId));

        // 3. Проверяем, привязан ли этот контакт к данному пользователю
        if (!loginUser.getContacts().contains(contact)) {
            throw new RuntimeException("You do not have permission to delete this contact or it's not in your list");
        }

        // 4. Мягкое удаление (ИСПРАВЛЕНО: правильный сеттер Lombok)
        contact.setDeleted(true);
        contactRepository.save(contact);

        // 5. Удаляем связь из коллекции пользователя
        loginUser.getContacts().remove(contact);
        loginRepository.save(loginUser);
    }
}