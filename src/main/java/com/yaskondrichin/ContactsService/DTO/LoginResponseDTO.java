package com.yaskondrichin.ContactsService.DTO;

import com.yaskondrichin.ContactsService.domain.model.Role;
import lombok.Data;

@Data
public class LoginResponseDTO{
    private Long id;
    private String login;
    private String Email;
    private String phone;
    private Role role;

}
