/**
 * REST API controllers
 *
 * <p>This package contains REST API controllers that handle HTTP requests
 * for the DigiStock application.</p>
 *
 * <p>All controllers:</p>
 * <ul>
 *   <li>Use the <code>/api/v1</code> base path for API versioning</li>
 *   <li>Return appropriate HTTP status codes</li>
 *   <li>Validate input using Bean Validation annotations</li>
 *   <li>Handle errors through the global exception handler</li>
 *   <li>Are documented with OpenAPI annotations</li>
 * </ul>
 *
 * <h3>API Versioning Strategy</h3>
 * <p>Controllers should be organized in version-specific packages (e.g., api.v1.controller)
 * to support API versioning and maintain backward compatibility.</p>
 *
 * @since 1.0.0
 */
package zw.co.digistock.controller;
