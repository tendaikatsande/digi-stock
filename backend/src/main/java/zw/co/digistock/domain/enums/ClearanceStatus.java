package zw.co.digistock.domain.enums;

/**
 * Status of a police clearance request
 */
public enum ClearanceStatus {
    /**
     * Clearance request submitted, awaiting police review
     */
    PENDING,

    /**
     * Police has approved the clearance
     */
    APPROVED,

    /**
     * Police has rejected the clearance (e.g., suspected stolen animal)
     */
    REJECTED,

    /**
     * Clearance has expired past its validity date
     */
    EXPIRED
}
