package com.yaskondrichin.ContactsService.DTO;

import com.yaskondrichin.ContactsService.domain.enums.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class AssignRoleDTO {

    @NotNull(message = "ID пользователя обязателен")
    private UUID userId;

    @NotNull(message = "Роль обязательна")
    private Role role; // Перечисление ROLE_USER, ROLE_ADMIN, которое мы создали ранее
}
