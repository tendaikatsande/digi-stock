# Email Configuration Guide

DigiStock uses Java Mail (Spring Boot Mail) to send email notifications for:
- Password reset requests
- Password change confirmations
- Welcome emails for new officer registrations

## Quick Start (Development with Mailpit)

**Mailpit is pre-configured and ready to use!** No configuration needed for development.

### 1. Start Services with Docker Compose

```bash
docker-compose up -d
```

This starts:
- PostgreSQL (port 5432)
- MinIO (ports 9000, 9001)
- **Mailpit** (SMTP: 1025, Web UI: 8025)

### 2. View Emails

Open your browser and navigate to:
- **Mailpit Web UI**: http://localhost:8025

All emails sent by the application will be captured by Mailpit and displayed in the web interface.

### 3. Test the Email Flow

```bash
# 1. Start the application
./mvnw spring-boot:run

# 2. Request a password reset
curl -X POST http://localhost:8080/api/v1/auth/forgot-password \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@digistock.zw"}'

# 3. Open Mailpit UI to see the email: http://localhost:8025
# 4. Copy the reset token from the email
# 5. Reset your password using the token
```

### 4. Mailpit Features

- ✅ Zero configuration required
- ✅ Web UI to view all emails
- ✅ Search and filter emails
- ✅ View HTML and plain text versions
- ✅ Check email headers and raw source
- ✅ API access for automated testing
- ✅ Mobile responsive interface

---

## Production / External SMTP Setup

For production or to use a real email service, configure these environment variables:

```bash
export SMTP_HOST=smtp.gmail.com          # Your SMTP server
export SMTP_PORT=587                      # SMTP port (587 for TLS)
export SMTP_USERNAME=your-email@gmail.com # SMTP username
export SMTP_PASSWORD=your-app-password    # SMTP password or app password
export SMTP_AUTH=true                     # Enable SMTP authentication
export SMTP_STARTTLS=true                 # Enable STARTTLS
export MAIL_FROM=noreply@digistock.zw    # From email address
export FRONTEND_URL=http://localhost:3000 # Frontend URL for reset links
```

### Gmail Setup

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
   export SMTP_AUTH=true
   export SMTP_STARTTLS=true
   export MAIL_FROM=your-email@gmail.com
   ```

### Other SMTP Providers

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

### Development Mode with Mailpit (Recommended)

Mailpit automatically captures all emails sent by the application. No configuration needed!

1. **Start services**: `docker-compose up -d`
2. **View emails**: Open http://localhost:8025
3. **Test the flow**: Send requests and watch emails appear in real-time

### Test Password Reset Flow

1. **Request Reset**:
   ```bash
   curl -X POST http://localhost:8080/api/v1/auth/forgot-password \
     -H "Content-Type: application/json" \
     -d '{"email":"admin@digistock.zw"}'
   ```

2. **Check Mailpit UI** at http://localhost:8025 - You'll see the email with the reset token

3. **Copy token from email** (click on the email in Mailpit to view it)

4. **Reset Password**:
   ```bash
   curl -X POST http://localhost:8080/api/v1/auth/reset-password \
     -H "Content-Type: application/json" \
     -d '{
       "token":"your-reset-token-from-email",
       "newPassword":"NewSecure123!",
       "confirmPassword":"NewSecure123!"
     }'
   ```

### Mailpit API for Automated Testing

Mailpit provides an API for automated testing:

```bash
# Get all messages
curl http://localhost:8025/api/v1/messages

# Search messages
curl http://localhost:8025/api/v1/search?query=password+reset

# Get specific message
curl http://localhost:8025/api/v1/message/{id}

# Delete all messages
curl -X DELETE http://localhost:8025/api/v1/messages
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
export SMTP_AUTH=true
export SMTP_STARTTLS=true
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
