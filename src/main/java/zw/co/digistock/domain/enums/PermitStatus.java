package zw.co.digistock.domain.enums;

/**
 * Status of a livestock movement permit
 */
public enum PermitStatus {
    /**
     * Permit request created, awaiting approval
     */
    PENDING,

    /**
     * Permit has been approved and issued
     */
    APPROVED,

    /**
     * Livestock is currently in transit under this permit
     */
    IN_TRANSIT,

    /**
     * Movement completed successfully, animal reached destination
     */
    COMPLETED,

    /**
     * Permit has expired past its validity date
     */
    EXPIRED,

    /**
     * Permit was cancelled (by owner or officer)
     */
    CANCELLED,

    /**
     * Permit was rejected
     */
    REJECTED
}
