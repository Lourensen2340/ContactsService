package com.yaskondrichin.ContactsService.DTO;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class AuthResponseDTO {
    private LoginResponseDTO user;
    private TokenResponseDTO tokens;
    private String generatedPassword;
}
