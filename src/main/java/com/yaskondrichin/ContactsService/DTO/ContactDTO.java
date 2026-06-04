package com.yaskondrichin.ContactsService.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactDTO {
    @Schema(hidden = true)
    private Long id;

    @NotBlank(message = "Имя не может быть пустым")
    private String name;

    private String surname;

    @NotBlank(message = "Телефон обязателен")
    private String phone;

    @Email(message = "Некорректный email")
    private String email;

    public ContactDTO(Long id, @NotBlank(message = "Имя обязательно") @Size(min = 2, message = "Имя должно быть длиннее 2-х символов") String name, @NotBlank(message = "Телефон обязателен") String phone) {
    }
}
