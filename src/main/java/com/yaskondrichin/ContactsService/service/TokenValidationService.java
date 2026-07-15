package com.yaskondrichin.ContactsService.service;

import com.yaskondrichin.ContactsService.DTO.TokenValidationDTO;
import java.time.Instant;
import java.util.UUID;

public interface TokenValidationService {
    void saveToken(String tokenValue, UUID userId, Instant expiryDate);
    TokenValidationDTO validateToken(String tokenValue);
    void revokeToken(String tokenValue);
    void revokeAllUserTokens(UUID userId);
}