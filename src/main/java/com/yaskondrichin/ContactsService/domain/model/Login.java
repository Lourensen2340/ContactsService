package com.yaskondrichin.ContactsService.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity


@Data // Эта аннотация Lombok автоматически создаст методы setEmail, setLogin и другие
@NoArgsConstructor
@AllArgsConstructor

public class Login {
    @ManyToMany(mappedBy = "users")
//    @JoinTable(
//            name = "user_contacts", // Название промежуточной таблицы в БД
//            joinColumns = @JoinColumn(name = "user_id"), // Внешний ключ для Login
//            inverseJoinColumns = @JoinColumn(name = "contact_id") // Внешний ключ для Contact
//    )
    private List<Contact> contacts = new ArrayList<>();
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