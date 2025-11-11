/**
 * Domain model - JPA entities
 *
 * <p>This package contains JPA entity classes that represent the domain model
 * of the DigiStock application.</p>
 *
 * <h3>Entity Best Practices</h3>
 * <ul>
 *   <li>Extend <code>BaseEntity</code> for common fields (id, timestamps, audit fields)</li>
 *   <li>Use Lombok annotations to reduce boilerplate</li>
 *   <li>Define bidirectional relationships carefully</li>
 *   <li>Use appropriate fetch types (LAZY by default for collections)</li>
 *   <li>Add database indexes for frequently queried fields</li>
 *   <li>Use cascade types carefully</li>
 *   <li>Never expose entities directly in REST APIs - use DTOs</li>
 * </ul>
 *
 * <h3>Subpackages</h3>
 * <ul>
 *   <li><code>base</code> - Base entity classes</li>
 *   <li><code>enums</code> - Enumeration types</li>
 * </ul>
 *
 * @since 1.0.0
 */
package zw.co.digistock.domain;
