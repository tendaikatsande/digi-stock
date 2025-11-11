/**
 * Exception handling
 *
 * <p>This package contains custom exception classes and the global exception handler
 * for the DigiStock application.</p>
 *
 * <h3>Exception Hierarchy</h3>
 * <ul>
 *   <li><code>BusinessException</code> - Base class for business logic errors</li>
 *   <li><code>ResourceNotFoundException</code> - When a requested resource is not found</li>
 *   <li><code>DuplicateResourceException</code> - When attempting to create a duplicate resource</li>
 * </ul>
 *
 * <h3>Global Exception Handler</h3>
 * <p>The <code>GlobalExceptionHandler</code> uses <code>@ControllerAdvice</code> to handle
 * exceptions globally and return appropriate HTTP responses.</p>
 *
 * @since 1.0.0
 */
package zw.co.digistock.exception;
