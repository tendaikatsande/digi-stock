package zw.co.digistock.util;

import java.util.regex.Pattern;

/**
 * Utility class for common validation operations
 */
public final class ValidationUtils {

    private ValidationUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^\\+?[0-9]{10,15}$"
    );

    private static final Pattern NATIONAL_ID_PATTERN = Pattern.compile(
        "^[0-9]{2}-[0-9]{6,7}[A-Z][0-9]{2}$" // Zimbabwe National ID format: 63-123456A12
    );

    /**
     * Validate email address
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Validate phone number
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone.replaceAll("\\s+", "")).matches();
    }

    /**
     * Validate Zimbabwe national ID
     */
    public static boolean isValidNationalId(String nationalId) {
        if (nationalId == null || nationalId.trim().isEmpty()) {
            return false;
        }
        return NATIONAL_ID_PATTERN.matcher(nationalId).matches();
    }

    /**
     * Check if string is null or empty
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * Normalize phone number (remove spaces, add country code if missing)
     */
    public static String normalizePhone(String phone) {
        if (phone == null) {
            return null;
        }

        phone = phone.replaceAll("\\s+", "");

        // Add Zimbabwe country code if missing
        if (!phone.startsWith("+") && !phone.startsWith("263")) {
            if (phone.startsWith("0")) {
                phone = "+263" + phone.substring(1);
            } else {
                phone = "+263" + phone;
            }
        }

        return phone;
    }
}
