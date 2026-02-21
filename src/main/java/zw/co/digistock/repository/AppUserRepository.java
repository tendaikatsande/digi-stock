package zw.co.digistock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zw.co.digistock.domain.AppUser;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for unified AppUser authentication queries.
 * Covers both Officers and Owners via JOINED inheritance.
 */
@Repository
public interface AppUserRepository extends JpaRepository<AppUser, UUID> {

    Optional<AppUser> findByEmail(String email);

    /**
     * Look up a user by their SHA-256 hashed reset token.
     */
    Optional<AppUser> findByResetToken(String hashedToken);

    boolean existsByEmail(String email);
}
