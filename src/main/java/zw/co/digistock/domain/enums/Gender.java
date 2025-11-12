package zw.co.digistock.domain.enums;

/**
 * Gender/Sex classification for livestock
 */
public enum Gender {
    /**
     * Male cattle (bull)
     */
    MALE("M", "Male"),

    /**
     * Female cattle (cow/heifer)
     */
    FEMALE("F", "Female"),

    /**
     * Castrated male (steer/ox)
     */
    CASTRATED("C", "Castrated");

    private final String code;
    private final String displayName;

    Gender(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Get Gender from database code (M, F, C)
     */
    public static Gender fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (Gender gender : values()) {
            if (gender.code.equalsIgnoreCase(code)) {
                return gender;
            }
        }
        throw new IllegalArgumentException("Unknown gender code: " + code);
    }

    /**
     * Get code for database storage
     */
    public String toCode() {
        return code;
    }
}
