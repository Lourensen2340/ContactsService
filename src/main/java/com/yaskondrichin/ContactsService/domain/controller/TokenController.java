package com.yaskondrichin.ContactsService.domain.controller;

import com.yaskondrichin.ContactsService.DTO.TokenValidationDTO;
import com.yaskondrichin.ContactsService.service.TokenValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tokens")
@RequiredArgsConstructor
public class TokenController {

    private final TokenValidationService tokenValidationService;

    @PostMapping("/validate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TokenValidationDTO> validate(@RequestParam String token) {
        return ResponseEntity.ok(tokenValidationService.validateToken(token));
    }

    @PostMapping("/revoke")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> revoke(@RequestParam String token) {
        tokenValidationService.revokeToken(token);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId.toString().equals(authentication.name)")
    public ResponseEntity<Void> revokeAllUserTokens(@PathVariable UUID userId) {
        tokenValidationService.revokeAllUserTokens(userId);
        return ResponseEntity.noContent().build();
    }
}