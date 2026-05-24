package com.vsg.repository;

import com.vsg.entity.OtpToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface OtpTokenRepository extends JpaRepository<OtpToken, UUID> {

    Optional<OtpToken> findFirstByEmailAndTypeAndUsedFalseAndExpiresAtAfterOrderByCreatedAtDesc(
            String email, String type, LocalDateTime now);

    Optional<OtpToken> findFirstByTokenAndTypeAndUsedFalseAndExpiresAtAfterOrderByCreatedAtDesc(
            String token, String type, LocalDateTime now);

    @Modifying
    @Query("DELETE FROM OtpToken o WHERE o.email = :email AND o.type = :type")
    void deleteByEmailAndType(@Param("email") String email, @Param("type") String type);
}
