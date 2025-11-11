package zw.co.digistock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * DigiStock - Digital Livestock Management System for Zimbabwe
 *
 * A comprehensive platform to:
 * - Replace paper livestock cards with digital records
 * - Enable real-time verification of cattle ownership and movement
 * - Combat cattle rustling through biometric identification
 * - Streamline movement permits and police clearances
 * - Provide traceability from birth to slaughter
 *
 * @author DigiStock Team
 * @version 1.0.0
 */
@SpringBootApplication
public class DigistockApplication {

    public static void main(String[] args) {
        SpringApplication.run(DigistockApplication.class, args);
    }
}
