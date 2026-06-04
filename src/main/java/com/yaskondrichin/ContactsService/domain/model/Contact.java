package com.yaskondrichin.ContactsService.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Table(name = "contacts")
@Data
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Это первичный ключ самого контакта

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // ИСПРАВЛЕНО: теперь колонка связи называется login_id
    private Login user; // Связь с сущностью Login

    @NotBlank(message = "Имя обязательно")
    @Size(min = 2, message = "Имя должно быть длинее 2-х символов")
    private String name;

    @NotBlank(message = "Имя обязательна") // Не забудьте исправить опечатку в тексте валидации "Имя обязательна" -> "Фамилия обязательна"
    private String surname;

    @NotBlank(message = "Телефон обязателен")
    private String phone;

    @Email(message = "Некорректный формат email")
    private String email;
}