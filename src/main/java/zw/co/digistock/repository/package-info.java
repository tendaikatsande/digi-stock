/**
 * Data access layer - JPA repositories
 *
 * <p>This package contains Spring Data JPA repository interfaces that provide
 * database access operations.</p>
 *
 * <h3>Repository Best Practices</h3>
 * <ul>
 *   <li>Extend <code>JpaRepository</code> for standard CRUD operations</li>
 *   <li>Use derived query methods for simple queries (e.g., findByTagCode)</li>
 *   <li>Use <code>@Query</code> annotation for complex queries</li>
 *   <li>Return <code>Page&lt;T&gt;</code> for paginated results</li>
 *   <li>Use <code>Optional&lt;T&gt;</code> for single result queries</li>
 *   <li>Avoid N+1 query problems using JOIN FETCH in queries</li>
 * </ul>
 *
 * @since 1.0.0
 */
package zw.co.digistock.repository;
