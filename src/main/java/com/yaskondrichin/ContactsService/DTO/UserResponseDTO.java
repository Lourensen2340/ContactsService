package com.yaskondrichin.ContactsService.DTO;

import lombok.Data;

@Data
public class UserResponseDTO {
    private Long id;
    private String username;
    private String email;
}