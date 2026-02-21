package zw.co.digistock.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import zw.co.digistock.domain.base.BaseEntity;
import zw.co.digistock.domain.enums.UserRole;

import java.time.LocalDateTime;

/**
 * Base authentication entity for all system users.
 * Officers and Owners extend this via JPA JOINED inheritance.
 *
 * The app_users table holds the common auth fields; child tables hold
 * domain-specific data. The child row id is a FK to app_users.id.
 */
@Entity
@Table(name = "app_users", indexes = {
    @Index(name = "idx_app_user_email", columnList = "email", unique = true)
})
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING, length = 30)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class AppUser extends BaseEntity {

    @Column(name = "email", unique = true, nullable = false, length = 150)
    private String email;

    @Column(name = "password_hash", length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 30)
    private UserRole role;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    /**
     * SHA-256 hash of the password reset token.
     * Using SHA-256 (not BCrypt) so the token is directly queryable by the DB.
     */
    @Column(name = "reset_token", length = 64)
    private String resetToken;

    @Column(name = "reset_token_expires_at")
    private LocalDateTime resetTokenExpiresAt;
}
