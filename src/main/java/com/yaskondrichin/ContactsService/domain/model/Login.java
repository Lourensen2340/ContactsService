package com.yaskondrichin.ContactsService.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity


@Data // Эта аннотация Lombok автоматически создаст методы setEmail, setLogin и другие
@NoArgsConstructor
@AllArgsConstructor
public class Login {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String login;

    @Column(nullable = false)
    private String pass;

    // ДОБАВЬТЕ ЭТО ПОЛЕ:
    @Column(unique = true)
    private String email;

    private String phone;
    @Enumerated(EnumType.STRING)
    private Role role;
}