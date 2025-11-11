/**
 * Business logic and service layer
 *
 * <p>This package contains service interfaces and implementations that encapsulate
 * business logic for the DigiStock application.</p>
 *
 * <h3>Service Layer Best Practices</h3>
 * <ul>
 *   <li><strong>Interface-based design:</strong> Define service interfaces and provide implementations
 *       in a separate <code>impl</code> package</li>
 *   <li><strong>Transaction management:</strong> Use <code>@Transactional</code> annotations appropriately</li>
 *   <li><strong>Read-only operations:</strong> Mark query methods as <code>@Transactional(readOnly = true)</code>
 *       for performance optimization</li>
 *   <li><strong>Business validation:</strong> Perform business rule validation in services</li>
 *   <li><strong>No direct entity exposure:</strong> Always return DTOs, never domain entities</li>
 *   <li><strong>Use mappers:</strong> Leverage MapStruct mappers for entity-DTO conversions</li>
 *   <li><strong>Pagination:</strong> Use Spring Data's <code>Pageable</code> for list operations</li>
 * </ul>
 *
 * <h3>Subpackages</h3>
 * <ul>
 *   <li><code>biometric</code> - Biometric matching and fingerprint operations</li>
 *   <li><code>qr</code> - QR code generation and management</li>
 *   <li><code>storage</code> - File storage operations (MinIO)</li>
 *   <li><code>impl</code> - Service implementations (recommended structure)</li>
 * </ul>
 *
 * @since 1.0.0
 */
package zw.co.digistock.service;
