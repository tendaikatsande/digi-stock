package zw.co.digistock.domain.enums;

/**
 * Sex classification for livestock. Created to satisfy references to `Sex` in DTOs.
 */
public enum Sex {

    /** Male cattle (bull) */
    MALE("M", "Male"),

    /** Female cattle (cow/heifer) */
    FEMALE("F", "Female"),

    /** Castrated male (steer/ox) */
    CASTRATED("C", "Castrated");

    private final String code;
    private final String displayName;

    Sex(String code, String displayName) {
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
     * Get Sex from database code (M, F, C)
     */
    public static Sex fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (Sex s : values()) {
            if (s.code.equalsIgnoreCase(code)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Unknown sex code: " + code);
    }

    /**
     * Get code for database storage
     */
    public String toCode() {
        return code;
    }
}

