package com.yaskondrichin.ContactsService.DTO;

import com.yaskondrichin.ContactsService.domain.model.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignRoleDTO {

    @NotNull(message = "ID пользователя обязателен")
    private Long userId;

    @NotNull(message = "Роль обязательна")
    private Role role; // Перечисление ROLE_USER, ROLE_ADMIN, которое мы создали ранее
}
