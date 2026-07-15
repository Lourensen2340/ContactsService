package com.yaskondrichin.ContactsService.domain.model;

import com.yaskondrichin.ContactsService.Utils.UuidV7Generator;
import com.yaskondrichin.ContactsService.domain.enums.Role;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import jakarta.persistence.Transient;
@Entity


@Data // Эта аннотация Lombok автоматически создаст методы setEmail, setLogin и другие
@NoArgsConstructor
@AllArgsConstructor

public class Login {
    @OneToMany(mappedBy = "login", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Contact> contacts = new ArrayList<>();
    @Id
    @GeneratedValue(generator = "UUIDv7")
    @org.hibernate.annotations.GenericGenerator(
            name = "UUIDv7",
            type = UuidV7Generator.class
    )
    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.VARCHAR)
    @Column(length = 36)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String login;

    @Column(nullable = false)
    private String pass;


    @Column(unique = true)
    private String email;

    private String phone;
    @Enumerated(EnumType.STRING)
    private Role role;
    @Transient
    private String rawPassword;
}