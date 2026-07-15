package com.yaskondrichin.ContactsService.DTO;

import lombok.Data;
import java.time.Instant;
import java.util.UUID;

@Data
public class TokenValidationDTO {
    private UUID id;
    private UUID userId;
    private Instant expiryDate;
    private boolean revoked;
    private boolean isValid; // Вычисляемое поле: !revoked && expiryDate.isAfter(now)
}