package com.yaskondrichin.ContactsService.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.annotations.Where;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;



@Data
@Setter
@Table(
        name = "contacts",
        uniqueConstraints = {
                // База данных сама отклонит запись, если у юзера уже есть контакт с таким телефоном
                @UniqueConstraint(columnNames = {"user_id", "phone"}),
                @UniqueConstraint(columnNames = {"user_id", "email"})
        }
)
@Entity
@SQLDelete(sql = "UPDATE contacts SET is_deleted = true WHERE id=?") // Автоматически превратит любой .delete() в UPDATE
@Where(clause = "is_deleted = false")
public class Contact {

    @Id

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Можно использовать java.util.UUID
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}) // Включаем каскадность
    @JoinTable(
            name = "contact_users", // Имя промежуточной таблицы в БД
            joinColumns = @JoinColumn(name = "contact_id"), // Внешний ключ для Contact
            inverseJoinColumns = @JoinColumn(name = "login_id") // Внешний ключ для Login
    )

    private List<Login> users = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @NotBlank(message = "Имя обязательно")
    @Size(min = 2, message = "Имя должно быть длинее 2-х символов")
    private String name;

    @NotBlank(message = "Имя обязательна") // Не забудьте исправить опечатку в тексте валидации "Имя обязательна" -> "Фамилия обязательна"
    private String surname;

    @NotBlank(message = "Телефон обязателен")
    private String phone;

    @Email(message = "Некорректный формат email")
    private String email;
    private boolean isDeleted;

    public void setDeleted(boolean deleted) {
        this.isDeleted = deleted; // убедитесь, что имя поля у вас совпадает (isDeleted)
    }
}