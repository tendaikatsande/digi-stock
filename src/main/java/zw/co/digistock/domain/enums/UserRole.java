package zw.co.digistock.domain.enums;

/**
 * User roles in the DigiStock system
 */
public enum UserRole {
    /**
     * System administrator with full access
     */
    ADMIN,

    /**
     * AGRITEX extension officer - can register livestock, enroll owners, issue permits
     */
    AGRITEX_OFFICER,

    /**
     * Police officer - can issue clearances, verify permits at checkpoints
     */
    POLICE_OFFICER,

    /**
     * Livestock owner - can view their animals, request permits
     */
    OWNER,

    /**
     * Veterinary inspector - can access health records, vaccination logs
     */
    VETERINARY_INSPECTOR
}
