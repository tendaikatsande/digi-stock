package zw.co.digistock.domain.enums;

/**
 * User roles in the DigiStock system
 */
public enum UserRole {
    /**
     * National administrator with full system access and governance
     */
    NATIONAL_ADMIN,

    /**
     * Provincial administrator with regional oversight and analytics
     */
    PROVINCIAL_ADMIN,

    /**
     * District administrator with local management and permit approvals
     */
    DISTRICT_ADMIN,

    /**
     * System administrator with full access (legacy support)
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
     * Veterinary officer - can access health records, vaccination logs, and disease reporting
     */
    VETERINARY_OFFICER,

    /**
     * Livestock owner - can view their animals, request permits
     */
    OWNER,

    /**
     * Veterinary inspector - can access health records, vaccination logs (legacy support)
     */
    VETERINARY_INSPECTOR,

    /**
     * Transporter - can verify movement permits and track routes
     */
    TRANSPORTER
}
