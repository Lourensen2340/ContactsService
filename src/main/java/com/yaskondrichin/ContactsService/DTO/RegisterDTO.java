package com.yaskondrichin.ContactsService.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class RegisterDTO {
    private String username;
    //private String generatedPassword;
    private String email;
    private String phone;
}
