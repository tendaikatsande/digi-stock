package zw.co.digistock.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.co.digistock.domain.AppUser;
import zw.co.digistock.domain.Officer;
import zw.co.digistock.domain.Owner;
import zw.co.digistock.domain.enums.UserRole;
import zw.co.digistock.dto.request.*;
import zw.co.digistock.dto.response.AuthResponse;
import zw.co.digistock.dto.response.MessageResponse;
import zw.co.digistock.exception.BusinessException;
import zw.co.digistock.exception.DuplicateResourceException;
import zw.co.digistock.exception.ResourceNotFoundException;
import zw.co.digistock.repository.AppUserRepository;
import zw.co.digistock.repository.OfficerRepository;
import zw.co.digistock.security.JwtUtil;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Service for authentication operations (login, register, password management).
 * Supports both Officer and Owner authentication via unified AppUser.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AppUserRepository appUserRepository;
    private final OfficerRepository officerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final EmailService emailService;

    /**
     * Authenticate a user (Officer or Owner) and return a JWT token.
     */
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());

        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());

        AppUser user = appUserRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new BusinessException("User not found"));

        if (!user.isActive()) {
            throw new BusinessException("Account is inactive");
        }

        String token = jwtUtil.generateToken(buildClaims(user), userDetails);
        log.info("Successfully authenticated user: {}", request.getEmail());

        return buildAuthResponse(user, token);
    }

    /**
     * Register a new Officer account. Requires admin role (enforced in SecurityConfig).
     */
    @Transactional
    public AuthResponse register(RegisterOfficerRequest request) {
        log.info("Registering new officer with email: {}", request.getEmail());

        if (appUserRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered: " + request.getEmail());
        }
        if (officerRepository.existsByOfficerCode(request.getOfficerCode())) {
            throw new DuplicateResourceException("Officer code already exists: " + request.getOfficerCode());
        }

        Officer officer = Officer.builder()
            .officerCode(request.getOfficerCode())
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .email(request.getEmail())
            .phoneNumber(request.getPhoneNumber())
            .passwordHash(passwordEncoder.encode(request.getPassword()))
            .role(request.getRole())
            .province(request.getProvince())
            .district(request.getDistrict())
            .active(true)
            .biometricEnrolled(false)
            .build();

        officer = officerRepository.save(officer);

        UserDetails userDetails = userDetailsService.loadUserByUsername(officer.getEmail());
        String token = jwtUtil.generateToken(buildClaims(officer), userDetails);

        log.info("Successfully registered officer: {}", officer.getEmail());
        emailService.sendWelcomeEmail(officer.getEmail(), officer.getFullName(), officer.getOfficerCode());

        return buildAuthResponse(officer, token);
    }

    /**
     * Initiate password reset. Stores SHA-256 hashed token for secure DB lookup.
     */
    @Transactional
    public MessageResponse forgotPassword(ForgotPasswordRequest request) {
        log.info("Password reset requested for email: {}", request.getEmail());

        AppUser user = appUserRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new ResourceNotFoundException("No account found with email: " + request.getEmail()));

        if (!user.isActive()) {
            throw new BusinessException("Account is inactive");
        }

        String plainToken = UUID.randomUUID().toString().replace("-", "");
        user.setResetToken(hashToken(plainToken));
        user.setResetTokenExpiresAt(LocalDateTime.now().plusHours(1));
        appUserRepository.save(user);

        log.info("Password reset token generated for: {}", request.getEmail());

        if (user instanceof Officer officer) {
            emailService.sendPasswordResetEmail(user.getEmail(), officer.getFullName(), plainToken);
        } else if (user instanceof Owner owner) {
            emailService.sendPasswordResetEmail(user.getEmail(), owner.getFullName(), plainToken);
        }

        return MessageResponse.builder()
            .message("Password reset instructions have been sent to your email. The reset link is valid for 1 hour.")
            .success(true)
            .build();
    }

    /**
     * Reset password using the plain token emailed to the user.
     * Looks up by SHA-256(token) — no full-table scan.
     */
    @Transactional
    public MessageResponse resetPassword(ResetPasswordRequest request) {
        log.info("Attempting password reset with token");

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("Passwords do not match");
        }

        AppUser user = appUserRepository.findByResetToken(hashToken(request.getToken()))
            .filter(u -> u.getResetTokenExpiresAt() != null &&
                         u.getResetTokenExpiresAt().isAfter(LocalDateTime.now()))
            .orElseThrow(() -> new BusinessException("Invalid or expired reset token"));

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        user.setResetToken(null);
        user.setResetTokenExpiresAt(null);
        appUserRepository.save(user);

        log.info("Password successfully reset for user: {}", user.getEmail());

        String fullName = user instanceof Officer o ? o.getFullName()
                        : user instanceof Owner ow ? ow.getFullName()
                        : user.getEmail();
        emailService.sendPasswordChangeConfirmation(user.getEmail(), fullName);

        return MessageResponse.builder()
            .message("Password has been successfully reset. You can now login with your new password.")
            .success(true)
            .build();
    }

    /**
     * Change password for an authenticated user.
     */
    @Transactional
    public MessageResponse changePassword(UUID userId, ChangePasswordRequest request) {
        log.info("Password change requested for user ID: {}", userId);

        AppUser user = appUserRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new BusinessException("Current password is incorrect");
        }
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("New passwords do not match");
        }
        if (passwordEncoder.matches(request.getNewPassword(), user.getPasswordHash())) {
            throw new BusinessException("New password must be different from current password");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        appUserRepository.save(user);

        log.info("Password successfully changed for user: {}", user.getEmail());

        String fullName = user instanceof Officer o ? o.getFullName()
                        : user instanceof Owner ow ? ow.getFullName()
                        : user.getEmail();
        emailService.sendPasswordChangeConfirmation(user.getEmail(), fullName);

        return MessageResponse.builder()
            .message("Password has been successfully changed.")
            .success(true)
            .build();
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private Map<String, Object> buildClaims(AppUser user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId().toString());
        claims.put("userType", user instanceof Officer ? "OFFICER" : "OWNER");
        claims.put("role", user.getRole().name());
        if (user instanceof Officer officer) {
            claims.put("officerCode", officer.getOfficerCode());
        }
        return claims;
    }

    private AuthResponse buildAuthResponse(AppUser user, String token) {
        AuthResponse.AuthResponseBuilder builder = AuthResponse.builder()
            .token(token)
            .tokenType("Bearer")
            .expiresIn(jwtUtil.getExpirationTime())
            .userId(user.getId())
            .email(user.getEmail())
            .role(user.getRole())
            .active(user.isActive())
            .userType(user instanceof Officer ? "OFFICER" : "OWNER");

        if (user instanceof Officer officer) {
            builder.fullName(officer.getFullName())
                   .officerCode(officer.getOfficerCode())
                   .province(officer.getProvince())
                   .district(officer.getDistrict());
        } else if (user instanceof Owner owner) {
            builder.fullName(owner.getFullName())
                   .province(owner.getProvince())
                   .district(owner.getDistrict());
        }

        return builder.build();
    }

    /**
     * Hash a token using SHA-256 for secure, queryable storage.
     * SHA-256 is deterministic (unlike BCrypt), enabling DB lookup by hash.
     */
    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                String h = Integer.toHexString(0xff & b);
                if (h.length() == 1) hex.append('0');
                hex.append(h);
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}
