package com.yaskondrichin.ContactsService.domain.repo;

import com.yaskondrichin.ContactsService.domain.model.Contact;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    @Query("SELECT c FROM Contact c JOIN c.users u WHERE u.id = :userId")
    List<Contact> findAllByUserIdAndIsDeletedFalse(Long userId);
}