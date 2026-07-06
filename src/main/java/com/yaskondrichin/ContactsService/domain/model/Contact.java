package com.yaskondrichin.ContactsService.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import java.util.UUID;

@Data
@Entity
@Table(
        name = "contacts",
        uniqueConstraints = {
                // ИСПРАВЛЕНО: теперь уникальность проверяется в рамках login_id
                @UniqueConstraint(columnNames = {"login_id", "phone"}),
                @UniqueConstraint(columnNames = {"login_id", "email"})
        }
)
@SQLDelete(sql = "UPDATE contacts SET is_deleted = true WHERE id=?")
@Where(clause = "is_deleted = false")
public class Contact {

    @Id
    @GeneratedValue(generator = "UUIDv7")
    @org.hibernate.annotations.GenericGenerator(
            name = "UUIDv7",
            type = com.yaskondrichin.ContactsService.domain.generator.UuidV7Generator.class
    )
    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.VARCHAR)
    @Column(length = 36)
    private UUID id;

    // ИСПРАВЛЕНО: Единственный владелец контакта — это аккаунт Login
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "login_id", nullable = false)
    private Login login;

    @NotBlank(message = "Имя обязательно")
    @Size(min = 2, message = "Имя должно быть длинее 2-х символов")
    private String name;

    @NotBlank(message = "Фамилия обязательна") // ИСПРАВЛЕНО: текст валидации
    private String surname;

    @NotBlank(message = "Телефон обязателен")
    private String phone;

    @Email(message = "Некорректный формат email")
    private String email;

    private boolean isDeleted = false;
}