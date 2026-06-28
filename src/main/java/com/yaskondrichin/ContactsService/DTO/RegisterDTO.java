package com.yaskondrichin.ContactsService.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class RegisterDTO {
    private String username;
    private String password;
    private String email;
    private String phone;
}
