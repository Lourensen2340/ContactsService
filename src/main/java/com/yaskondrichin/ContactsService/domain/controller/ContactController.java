package com.yaskondrichin.ContactsService.domain.controller;

import com.yaskondrichin.ContactsService.DTO.ContactDTO;
import com.yaskondrichin.ContactsService.Utils.SecurityUtils;
import com.yaskondrichin.ContactsService.config.LoggedInUserId;
import com.yaskondrichin.ContactsService.service.ContactService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/contacts")
@Tag(name = "Контакт-менеджер", description = "Управление списком контактов")
@RequiredArgsConstructor
public class ContactController {


    private final ContactService contactService;
    private final SecurityUtils securityUtils;

    @GetMapping

    @Operation(summary = "Получить все контакты текущего пользователя")
    public ResponseEntity<List<ContactDTO>> getAll(@AuthenticationPrincipal Jwt jwt) {

        Long userId = jwt.getClaim("userId");

        List<ContactDTO> contacts = contactService.findAllByUserId(userId);
        return ResponseEntity.ok(contacts);
    }

    @PostMapping
    @Operation(summary = "Создать новый контакт")
    public ResponseEntity<ContactDTO> createContact(
            @RequestBody ContactDTO dto,
            @AuthenticationPrincipal Jwt jwt) {

        // 1. Просто вызываем вспомогательный метод. Здесь никакой паники компилятора.
        final Long userId = securityUtils.getUserIdFromJwt(jwt);

        // 2. Передаем чистый Long userId в сервис
        ContactDTO response = contactService.create(dto, userId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{contactId}") // 1. ОБЯЗАТЕЛЬНО добавляем путь для переменной
    public ResponseEntity<ContactDTO> updateContact(
            @PathVariable Long contactId,
            @AuthenticationPrincipal Jwt jwt, // 2. ОБЯЗАТЕЛЬНО объявляем jwt в параметрах
            @Valid @RequestBody ContactDTO contactDTO
    ) {
        System.out.println("\n=== [DEBUG] СТРУКТУРА JWT ТОКЕНА ===");
        jwt.getClaims().forEach((key, value) -> {
            System.out.println("Клейм: [" + key + "] -> Значение: [" + value + "]");
        });
        System.out.println("====================================\n");
        // ID пользователя (владельца) остается Long, это правильно!
        Long userId = securityUtils.getUserIdFromJwt(jwt);

        // 3. Исправляем имя переменной с dto на contactDTO
        ContactDTO updated = contactService.update(contactId, contactDTO, userId);

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{contactId}")
    public ResponseEntity<?> deleteContact(
            @PathVariable Long contactId,
            @AuthenticationPrincipal Jwt jwt) { // 1. Принимаем JWT-токен

        // 2. Вызываем ваш приватный метод для получения ID пользователя из токена
        Long userId = securityUtils.getUserIdFromJwt(jwt);

        // 3. Передаем ОБА параметра в сервис (теперь компилятор будет доволен!)
        contactService.deleteContact(contactId, userId);

        return ResponseEntity.ok().build();
    }

}