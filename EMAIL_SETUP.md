# Email Configuration Guide

DigiStock uses Java Mail (Spring Boot Mail) to send email notifications for:
- Password reset requests
- Password change confirmations
- Welcome emails for new officer registrations

## Quick Start

### 1. Environment Variables

Set these environment variables to configure email:

```bash
export SMTP_HOST=smtp.gmail.com          # Your SMTP server
export SMTP_PORT=587                      # SMTP port (587 for TLS)
export SMTP_USERNAME=your-email@gmail.com # SMTP username
export SMTP_PASSWORD=your-app-password    # SMTP password or app password
export MAIL_FROM=noreply@digistock.zw    # From email address
export FRONTEND_URL=http://localhost:3000 # Frontend URL for reset links
```

### 2. Gmail Setup (Recommended for Development)

If using Gmail:

1. **Enable 2-Factor Authentication** on your Google account
2. **Generate an App Password**:
   - Go to: https://myaccount.google.com/apppasswords
   - Select "Mail" and "Other (Custom name)"
   - Enter "DigiStock" as the app name
   - Click "Generate"
   - Copy the 16-character password

3. **Set Environment Variables**:
   ```bash
   export SMTP_HOST=smtp.gmail.com
   export SMTP_PORT=587
   export SMTP_USERNAME=your-email@gmail.com
   export SMTP_PASSWORD=your-16-char-app-password
   export MAIL_FROM=your-email@gmail.com
   ```

### 3. Other SMTP Providers

#### SendGrid
```bash
export SMTP_HOST=smtp.sendgrid.net
export SMTP_PORT=587
export SMTP_USERNAME=apikey
export SMTP_PASSWORD=your-sendgrid-api-key
```

#### Mailgun
```bash
export SMTP_HOST=smtp.mailgun.org
export SMTP_PORT=587
export SMTP_USERNAME=postmaster@your-domain.mailgun.org
export SMTP_PASSWORD=your-mailgun-password
```

#### AWS SES
```bash
export SMTP_HOST=email-smtp.us-east-1.amazonaws.com
export SMTP_PORT=587
export SMTP_USERNAME=your-ses-smtp-username
export SMTP_PASSWORD=your-ses-smtp-password
```

#### Outlook/Office 365
```bash
export SMTP_HOST=smtp.office365.com
export SMTP_PORT=587
export SMTP_USERNAME=your-email@outlook.com
export SMTP_PASSWORD=your-password
```

## Email Templates

The system sends the following emails:

### 1. Password Reset Email
- **Trigger**: POST `/api/v1/auth/forgot-password`
- **Contains**: Reset link and token (valid for 1 hour)
- **Template**: Plain text with reset link

### 2. Password Change Confirmation
- **Trigger**: Successful password change or reset
- **Contains**: Confirmation message and security notice
- **Template**: Plain text confirmation

### 3. Welcome Email
- **Trigger**: New officer registration
- **Contains**: Welcome message, officer code, and login link
- **Template**: Plain text welcome message

## Testing

### Development Mode

In development, email sending is non-blocking and logs are written for debugging:

```properties
spring.mail.test-connection=false
```

To test without an actual SMTP server, check application logs for email content.

### Test Password Reset Flow

1. **Request Reset**:
   ```bash
   curl -X POST http://localhost:8080/api/v1/auth/forgot-password \
     -H "Content-Type: application/json" \
     -d '{"email":"admin@digistock.zw"}'
   ```

2. **Check Logs** for the reset token (if SMTP not configured)

3. **Reset Password**:
   ```bash
   curl -X POST http://localhost:8080/api/v1/auth/reset-password \
     -H "Content-Type: application/json" \
     -d '{
       "token":"your-reset-token",
       "newPassword":"NewSecure123!",
       "confirmPassword":"NewSecure123!"
     }'
   ```

## Production Configuration

For production, use environment-specific configuration:

1. **Use a dedicated email service** (SendGrid, AWS SES, Mailgun)
2. **Set proper FROM address** with your domain
3. **Enable SPF/DKIM/DMARC** for email authentication
4. **Monitor email delivery** rates and bounces
5. **Set up email templates** using HTML (optional enhancement)

### Example Production Settings

```bash
export SMTP_HOST=smtp.sendgrid.net
export SMTP_PORT=587
export SMTP_USERNAME=apikey
export SMTP_PASSWORD=${SENDGRID_API_KEY}
export MAIL_FROM=noreply@digistock.gov.zw
export FRONTEND_URL=https://app.digistock.gov.zw
```

## Troubleshooting

### Emails Not Sending

1. **Check SMTP credentials**: Verify username/password
2. **Check firewall**: Ensure port 587 is open
3. **Check logs**: Look for JavaMail exceptions
4. **Test connection**: Temporarily set `spring.mail.test-connection=true`
5. **Gmail specific**: Ensure App Password is used (not regular password)

### Emails Going to Spam

1. **Set up SPF record** for your domain
2. **Enable DKIM signing** with your email provider
3. **Set up DMARC** policy
4. **Use verified FROM address**
5. **Warm up your IP** (for dedicated IPs)

### Connection Timeout

Increase timeout values in `application.properties`:

```properties
spring.mail.properties.mail.smtp.connectiontimeout=10000
spring.mail.properties.mail.smtp.timeout=10000
spring.mail.properties.mail.smtp.writetimeout=10000
```

## Security Notes

- **Never commit SMTP credentials** to version control
- **Use environment variables** or secrets management
- **Rotate passwords** regularly
- **Use App Passwords** for Gmail (not account password)
- **Enable TLS** (already configured)
- **Monitor for abuse** to prevent spam

## Future Enhancements

Potential improvements:
- [ ] HTML email templates with Thymeleaf
- [ ] Email queue with retry mechanism
- [ ] Email delivery tracking
- [ ] Unsubscribe management
- [ ] Email templates management UI
- [ ] Multi-language support
- [ ] Email analytics dashboard
