package zw.co.digistock.util;

/**
 * Application-wide constants
 */
public final class Constants {

    private Constants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    // API Versioning
    public static final String API_V1_BASE_PATH = "/api/v1";

    // Pagination Defaults
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;
    public static final String DEFAULT_PAGE_NUMBER = "0";
    public static final String DEFAULT_PAGE_SIZE_STR = "20";
    public static final String DEFAULT_SORT_BY = "createdAt";
    public static final String DEFAULT_SORT_DIRECTION = "DESC";

    // Date Formats
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String TIME_ZONE = "Africa/Harare";

    // Validation Messages
    public static final String REQUIRED_FIELD = "This field is required";
    public static final String INVALID_FORMAT = "Invalid format";
    public static final String INVALID_EMAIL = "Invalid email address";
    public static final String INVALID_PHONE = "Invalid phone number";

    // File Upload
    public static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    public static final String[] ALLOWED_IMAGE_TYPES = {"image/jpeg", "image/jpg", "image/png"};
    public static final String[] ALLOWED_DOCUMENT_TYPES = {"application/pdf"};

    // Security
    public static final String JWT_TOKEN_PREFIX = "Bearer ";
    public static final String AUTHORIZATION_HEADER = "Authorization";

    // Cache Names
    public static final String CACHE_OWNERS = "owners";
    public static final String CACHE_LIVESTOCK = "livestock";
    public static final String CACHE_PERMITS = "permits";
    public static final String CACHE_CLEARANCES = "clearances";
}
