package com.yaskondrichin.ContactsService.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.GeneratedValue;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactDTO {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private UUID id;
    @NotBlank(message = "Имя не должно быть пустым")
    @Size(min = 2, message = "Имя должно содержать минимум 2 символа")
    private String name;

    @NotBlank(message = "Фамилия обязательна")
    private String surname;

    @NotBlank(message = "Телефон обязателен")
    private String phone;

    @Email(message = "Некорректный формат email")
    private String email;
}
