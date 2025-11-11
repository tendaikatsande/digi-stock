/**
 * Data Transfer Objects (DTOs)
 *
 * <p>This package contains DTO classes used for API requests and responses.
 * DTOs provide a layer of abstraction between the domain model and the API.</p>
 *
 * <h3>DTO Best Practices</h3>
 * <ul>
 *   <li>Separate request and response DTOs in dedicated subpackages</li>
 *   <li>Use Bean Validation annotations for input validation</li>
 *   <li>Keep DTOs immutable where possible</li>
 *   <li>Use Lombok to reduce boilerplate</li>
 *   <li>Never include sensitive information in responses</li>
 *   <li>Use nested DTOs for related entities (avoid deep nesting)</li>
 * </ul>
 *
 * <h3>Subpackages</h3>
 * <ul>
 *   <li><code>request</code> - Request DTOs for API inputs</li>
 *   <li><code>response</code> - Response DTOs for API outputs</li>
 * </ul>
 *
 * @since 1.0.0
 */
package zw.co.digistock.dto;
