package com.yaskondrichin.ContactsService.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class LoginRequestDTO {
    private String login;
    private String email;
    private String password;
    @NotBlank(message = "Номер телефона не должен быть пустым")
    @Pattern(
            regexp = "^\\+375\\d{9}$",
            message = "Номер телефона должен быть в формате +375XXXXXXXXX")
    private String phone;

}

