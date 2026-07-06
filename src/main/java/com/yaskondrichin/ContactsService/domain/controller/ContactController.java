package com.yaskondrichin.ContactsService.domain.controller;

import com.yaskondrichin.ContactsService.DTO.ContactDTO;
import com.yaskondrichin.ContactsService.DTO.ContactDTO;
import com.yaskondrichin.ContactsService.service.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID; // ИСПРАВЛЕНО

@RestController
@RequestMapping("/api/contacts")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    @GetMapping
    public ResponseEntity<List<ContactDTO>> getUserContacts(@AuthenticationPrincipal Jwt jwt) {
        // ИСПРАВЛЕНО: Извлекаем userId из токена как UUID
        UUID userId = UUID.fromString(jwt.getSubject());
        return ResponseEntity.ok(contactService.findAllByUserId(userId));
    }

    @PostMapping
    public ResponseEntity<ContactDTO> createContact(@RequestBody ContactDTO dto, @AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        return ResponseEntity.ok(contactService.create(dto, userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContactDTO> updateContact(
            @PathVariable("id") UUID contactId, // ИСПРАВЛЕНО: UUID
            @RequestBody ContactDTO dto,
            @AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        return ResponseEntity.ok(contactService.update(contactId, dto, userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContact(
            @PathVariable("id") UUID contactId, // ИСПРАВЛЕНО: UUID
            @AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        contactService.deleteContact(contactId, userId);
        return ResponseEntity.noContent().build();
    }
}