package com.yaskondrichin.ContactsService.DTO;

import com.yaskondrichin.ContactsService.domain.enums.Role;
import lombok.Data;

import java.util.UUID;

@Data
public class LoginResponseDTO{
    private UUID id;
    private String login;
    private String Email;
    private String phone;
    private Role role;

}
