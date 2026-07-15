package com.yaskondrichin.ContactsService.domain.repo;

import com.yaskondrichin.ContactsService.domain.model.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserTokenRepository extends JpaRepository<UserToken, UUID> {
    Optional<UserToken> findByTokenValue(String tokenValue);
    void deleteByUserId(UUID userId);
}
