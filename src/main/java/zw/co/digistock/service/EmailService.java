package zw.co.digistock.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Service for sending emails
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.from:noreply@digistock.zw}")
    private String fromEmail;

    @Value("${digistock.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    /**
     * Send password reset email
     *
     * @param toEmail      Recipient email
     * @param recipientName Recipient name
     * @param resetToken   Password reset token
     */
    public void sendPasswordResetEmail(String toEmail, String recipientName, String resetToken) {
        log.info("Sending password reset email to: {}", toEmail);

        try {
            String resetLink = frontendUrl + "/reset-password?token=" + resetToken;

            String subject = "DigiStock - Password Reset Request";
            String body = buildPasswordResetEmailBody(recipientName, resetLink, resetToken);

            sendSimpleEmail(toEmail, subject, body);

            log.info("Password reset email sent successfully to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send password reset email to: {}", toEmail, e);
            // Don't throw exception - we don't want to fail the password reset flow
            // In production, you might want to queue this for retry
        }
    }

    /**
     * Send simple text email
     */
    private void sendSimpleEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }

    /**
     * Build password reset email body
     */
    private String buildPasswordResetEmailBody(String recipientName, String resetLink, String resetToken) {
        return String.format("""
            Dear %s,

            You recently requested to reset your password for your DigiStock account.

            Click the link below to reset your password:
            %s

            If you prefer, you can also use this reset token directly:
            %s

            This password reset link is valid for 1 hour.

            If you did not request a password reset, please ignore this email or contact support if you have concerns.

            Thank you,
            The DigiStock Team

            ---
            This is an automated message, please do not reply to this email.
            For support, contact: support@digistock.zw
            """, recipientName, resetLink, resetToken);
    }

    /**
     * Send welcome email to new officers
     */
    public void sendWelcomeEmail(String toEmail, String recipientName, String officerCode) {
        log.info("Sending welcome email to: {}", toEmail);

        try {
            String subject = "Welcome to DigiStock!";
            String body = String.format("""
                Dear %s,

                Welcome to DigiStock - Digital Livestock Management System for Zimbabwe!

                Your officer account has been successfully created:
                - Officer Code: %s
                - Email: %s

                You can now log in to the system and start using DigiStock services.

                Login URL: %s/login

                If you have any questions or need assistance, please contact our support team.

                Thank you,
                The DigiStock Team

                ---
                This is an automated message, please do not reply to this email.
                For support, contact: support@digistock.zw
                """, recipientName, officerCode, toEmail, frontendUrl);

            sendSimpleEmail(toEmail, subject, body);

            log.info("Welcome email sent successfully to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send welcome email to: {}", toEmail, e);
        }
    }

    /**
     * Send password change confirmation email
     */
    public void sendPasswordChangeConfirmation(String toEmail, String recipientName) {
        log.info("Sending password change confirmation to: {}", toEmail);

        try {
            String subject = "DigiStock - Password Changed Successfully";
            String body = String.format("""
                Dear %s,

                This email confirms that your DigiStock password was successfully changed.

                If you did not make this change, please contact our support team immediately.

                For your security, we recommend:
                - Using a strong, unique password
                - Not sharing your password with anyone
                - Changing your password regularly

                Thank you,
                The DigiStock Team

                ---
                This is an automated message, please do not reply to this email.
                For support, contact: support@digistock.zw
                """, recipientName);

            sendSimpleEmail(toEmail, subject, body);

            log.info("Password change confirmation sent successfully to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send password change confirmation to: {}", toEmail, e);
        }
    }
}
