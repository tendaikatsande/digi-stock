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
import zw.co.digistock.domain.Officer;
import zw.co.digistock.dto.request.*;
import zw.co.digistock.dto.response.AuthResponse;
import zw.co.digistock.dto.response.MessageResponse;
import zw.co.digistock.exception.BusinessException;
import zw.co.digistock.exception.DuplicateResourceException;
import zw.co.digistock.exception.ResourceNotFoundException;
import zw.co.digistock.repository.OfficerRepository;
import zw.co.digistock.security.JwtUtil;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Service for authentication operations
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final OfficerRepository officerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final EmailService emailService;

    /**
     * Authenticate officer and return JWT token
     */
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());

        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
            )
        );

        // Load user details
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());

        // Get officer details
        Officer officer = officerRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new BusinessException("Officer not found"));

        if (!officer.isActive()) {
            throw new BusinessException("Officer account is inactive");
        }

        // Generate JWT token with additional claims
        Map<String, Object> claims = new HashMap<>();
        claims.put("officerId", officer.getId().toString());
        claims.put("role", officer.getRole().name());
        claims.put("officerCode", officer.getOfficerCode());

        String token = jwtUtil.generateToken(claims, userDetails);

        log.info("Successfully authenticated officer: {}", request.getEmail());

        return buildAuthResponse(officer, token);
    }

    /**
     * Register a new officer
     */
    @Transactional
    public AuthResponse register(RegisterOfficerRequest request) {
        log.info("Registering new officer with email: {}", request.getEmail());

        // Check if email already exists
        if (officerRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered: " + request.getEmail());
        }

        // Check if officer code already exists
        if (officerRepository.existsByOfficerCode(request.getOfficerCode())) {
            throw new DuplicateResourceException("Officer code already exists: " + request.getOfficerCode());
        }

        // Create new officer
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

        // Generate JWT token
        UserDetails userDetails = userDetailsService.loadUserByUsername(officer.getEmail());

        Map<String, Object> claims = new HashMap<>();
        claims.put("officerId", officer.getId().toString());
        claims.put("role", officer.getRole().name());
        claims.put("officerCode", officer.getOfficerCode());

        String token = jwtUtil.generateToken(claims, userDetails);

        log.info("Successfully registered officer: {}", officer.getEmail());

        // Send welcome email
        emailService.sendWelcomeEmail(officer.getEmail(), officer.getFullName(), officer.getOfficerCode());

        return buildAuthResponse(officer, token);
    }

    /**
     * Initiate password reset process
     */
    @Transactional
    public MessageResponse forgotPassword(ForgotPasswordRequest request) {
        log.info("Password reset requested for email: {}", request.getEmail());

        Officer officer = officerRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new ResourceNotFoundException("No account found with email: " + request.getEmail()));

        if (!officer.isActive()) {
            throw new BusinessException("Officer account is inactive");
        }

        // Generate reset token (UUID + timestamp for uniqueness)
        String resetToken = UUID.randomUUID().toString().replace("-", "");

        // Hash the token before storing (security best practice)
        String hashedToken = passwordEncoder.encode(resetToken);

        // Token expires in 1 hour
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(1);

        officer.setResetToken(hashedToken);
        officer.setResetTokenExpiresAt(expiresAt);
        officerRepository.save(officer);

        log.info("Password reset token generated for: {}", request.getEmail());

        // Send password reset email with the plain token (before hashing)
        emailService.sendPasswordResetEmail(officer.getEmail(), officer.getFullName(), resetToken);

        return MessageResponse.builder()
            .message("Password reset instructions have been sent to your email. The reset link is valid for 1 hour.")
            .success(true)
            .build();
    }

    /**
     * Reset password using reset token
     */
    @Transactional
    public MessageResponse resetPassword(ResetPasswordRequest request) {
        log.info("Attempting password reset with token");

        // Validate passwords match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("Passwords do not match");
        }

        // Find officer with non-expired reset token
        Officer officer = officerRepository.findAll().stream()
            .filter(o -> o.getResetToken() != null &&
                        o.getResetTokenExpiresAt() != null &&
                        o.getResetTokenExpiresAt().isAfter(LocalDateTime.now()) &&
                        passwordEncoder.matches(request.getToken(), o.getResetToken()))
            .findFirst()
            .orElseThrow(() -> new BusinessException("Invalid or expired reset token"));

        // Update password
        officer.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));

        // Clear reset token
        officer.setResetToken(null);
        officer.setResetTokenExpiresAt(null);

        officerRepository.save(officer);

        log.info("Password successfully reset for officer: {}", officer.getEmail());

        // Send confirmation email
        emailService.sendPasswordChangeConfirmation(officer.getEmail(), officer.getFullName());

        return MessageResponse.builder()
            .message("Password has been successfully reset. You can now login with your new password.")
            .success(true)
            .build();
    }

    /**
     * Change password for authenticated user
     */
    @Transactional
    public MessageResponse changePassword(UUID officerId, ChangePasswordRequest request) {
        log.info("Password change requested for officer ID: {}", officerId);

        Officer officer = officerRepository.findById(officerId)
            .orElseThrow(() -> new ResourceNotFoundException("Officer not found"));

        // Validate current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), officer.getPasswordHash())) {
            throw new BusinessException("Current password is incorrect");
        }

        // Validate new passwords match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("New passwords do not match");
        }

        // Validate new password is different from current
        if (passwordEncoder.matches(request.getNewPassword(), officer.getPasswordHash())) {
            throw new BusinessException("New password must be different from current password");
        }

        // Update password
        officer.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        officerRepository.save(officer);

        log.info("Password successfully changed for officer: {}", officer.getEmail());

        // Send confirmation email
        emailService.sendPasswordChangeConfirmation(officer.getEmail(), officer.getFullName());

        return MessageResponse.builder()
            .message("Password has been successfully changed.")
            .success(true)
            .build();
    }

    /**
     * Build authentication response
     */
    private AuthResponse buildAuthResponse(Officer officer, String token) {
        return AuthResponse.builder()
            .token(token)
            .tokenType("Bearer")
            .expiresIn(jwtUtil.getExpirationTime())
            .officerId(officer.getId())
            .email(officer.getEmail())
            .fullName(officer.getFullName())
            .officerCode(officer.getOfficerCode())
            .role(officer.getRole())
            .province(officer.getProvince())
            .district(officer.getDistrict())
            .active(officer.isActive())
            .build();
    }
}
