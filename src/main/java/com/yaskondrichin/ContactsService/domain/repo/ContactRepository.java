package com.yaskondrichin.ContactsService.domain.repo;

import com.yaskondrichin.ContactsService.domain.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    // Метод поиска контактов по ID связи с User
    List<Contact> findAllByUserId(Long userId);
}