package com.yaskondrichin.ContactsService.domain.controller;

import com.yaskondrichin.ContactsService.DTO.ContactDTO;
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

@RestController
@RequestMapping("/api/v1/contacts")
@Tag(name = "Контакт-менеджер", description = "Управление списком контактов")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Получить все контакты текущего пользователя")
    public ResponseEntity<List<ContactDTO>> getAll(
            @Parameter(hidden = true) @LoggedInUserId Long userId
    ) {
        List<ContactDTO> contacts = contactService.findAllByUserId(userId);
        return ResponseEntity.ok(contacts);
    }

    @PostMapping
    @Operation(summary = "Создать новый контакт")
    public ResponseEntity<ContactDTO> createContact(
            @RequestBody ContactDTO dto,
            @AuthenticationPrincipal Jwt jwt) {

        // Получаем userId из клеймов JWT токена
        Long userId = jwt.getClaim("userId");

        // Передаем ContactDTO и userId в сервис
        ContactDTO response = contactService.save(dto, userId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContactDTO> updateContact(
            @PathVariable Long id,
            @RequestBody ContactDTO dto,
            @AuthenticationPrincipal Jwt jwt) { // Принимаем Jwt из контекста безопасности

        // Извлекаем userId из клейма токена прямо в контроллере
        Long userId = jwt.getClaim("userId");

        // Передаем id контакта, пришедший DTO и извлеченный числовой userId в сервис
        ContactDTO updated = contactService.update(id, dto, userId);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить контакт")
    public ResponseEntity<Void> deleteContact(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) { // Берем JWT из контекста

        // ВНИМАТЕЛЬНО проверьте эту строку:
        Long userId = jwt.getClaim("userId");

        // Передаем id контакта и извлеченный userId
        contactService.delete(id, userId);

        return ResponseEntity.noContent().build();
    }


}