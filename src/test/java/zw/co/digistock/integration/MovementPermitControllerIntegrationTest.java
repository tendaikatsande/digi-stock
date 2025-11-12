package zw.co.digistock.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import zw.co.digistock.domain.Livestock;
import zw.co.digistock.domain.MovementPermit;
import zw.co.digistock.domain.Officer;
import zw.co.digistock.domain.Owner;
import zw.co.digistock.domain.enums.Gender;
import zw.co.digistock.domain.enums.PermitStatus;
import zw.co.digistock.domain.enums.UserRole;
import zw.co.digistock.repository.LivestockRepository;
import zw.co.digistock.repository.MovementPermitRepository;
import zw.co.digistock.repository.OfficerRepository;
import zw.co.digistock.repository.OwnerRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for MovementPermitController pagination
 */
@DisplayName("Movement Permit Controller Integration Tests - Pagination")
class MovementPermitControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MovementPermitRepository permitRepository;

    @Autowired
    private LivestockRepository livestockRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private OfficerRepository officerRepository;

    private Owner testOwner;
    private Livestock testLivestock;
    private Officer testOfficer;
    private List<MovementPermit> testPermits;

    @BeforeEach
    void setUp() {
        // Clean up
        permitRepository.deleteAll();
        livestockRepository.deleteAll();
        ownerRepository.deleteAll();
        officerRepository.deleteAll();

        // Create test owner
        testOwner = Owner.builder()
            .nationalId("63-234567B23")
            .firstName("Alice")
            .lastName("Johnson")
            .phoneNumber("+263773456789")
            .email("alice.johnson@example.com")
            .district("Bulawayo")
            .province("Bulawayo")
            .address("456 Test Avenue")
            .build();
        testOwner = ownerRepository.save(testOwner);

        // Create test officer
        testOfficer = Officer.builder()
            .officerCode("AO-002")
            .firstName("Bob")
            .lastName("Wilson")
            .phoneNumber("+263774567890")
            .email("bob.wilson@agritex.gov.zw")
            .role(UserRole.AGRITEX_OFFICER)
            .district("Bulawayo")
            .province("Bulawayo")
            .active(true)
            .build();
        testOfficer = officerRepository.save(testOfficer);

        // Create test livestock
        testLivestock = Livestock.builder()
            .tagCode("ZW-BU-005678")
            .name("Thunder")
            .breed("Angus")
            .sex("M")
            .birthDate(LocalDate.now().minusYears(3))
            .owner(testOwner)
            .stolen(false)
            .build();
        testLivestock = livestockRepository.save(testLivestock);

        // Create test permits
        testPermits = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            PermitStatus status;
            if (i % 3 == 0) {
                status = PermitStatus.APPROVED;
            } else if (i % 3 == 1) {
                status = PermitStatus.COMPLETED;
            } else {
                status = PermitStatus.CANCELLED;
            }

            MovementPermit permit = MovementPermit.builder()
                .permitNumber(String.format("MP-BU-%06d", i + 1))
                .livestock(testLivestock)
                .issuedBy(testOfficer)
                .status(status)
                .fromLocation("Bulawayo")
                .toLocation("Harare")
                .purpose("Sale")
                .issuedAt(LocalDateTime.now().minusDays(i))
                .validFrom(LocalDate.now().minusDays(i))
                .validUntil(LocalDate.now().plusDays(7 - i))
                .build();
            testPermits.add(permitRepository.save(permit));
        }
    }

    @Test
    @DisplayName("GET /api/v1/permits/livestock/{id} - Should return paginated permits")
    void testGetPermitsByLivestock_WithPagination() throws Exception {
        // Given: Livestock ID
        UUID livestockId = testLivestock.getId();

        // When: Request first page
        ResultActions result = mockMvc.perform(get("/api/v1/permits/livestock/{livestockId}", livestockId)
            .param("page", "0")
            .param("size", "15")
            .contentType(MediaType.APPLICATION_JSON));

        // Then: Should return paginated results
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content.length()").value(15))
            .andExpect(jsonPath("$.totalElements").value(30))
            .andExpect(jsonPath("$.totalPages").value(2))
            .andExpect(jsonPath("$.pageable.pageNumber").value(0))
            .andExpect(jsonPath("$.pageable.pageSize").value(15));
    }

    @Test
    @DisplayName("GET /api/v1/permits/livestock/{id} - Should sort by issuedAt by default")
    void testGetPermitsByLivestock_DefaultSorting() throws Exception {
        // Given: Livestock ID
        UUID livestockId = testLivestock.getId();

        // When: Request without sort parameters
        ResultActions result = mockMvc.perform(get("/api/v1/permits/livestock/{livestockId}", livestockId)
            .param("page", "0")
            .param("size", "10")
            .contentType(MediaType.APPLICATION_JSON));

        // Then: Should be sorted by issuedAt descending
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.sort.sorted").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/permits/status/{status} - Should filter by status with pagination")
    void testGetPermitsByStatus_WithPagination() throws Exception {
        // When: Request ACTIVE permits
        ResultActions result = mockMvc.perform(get("/api/v1/permits/status/ACTIVE")
            .param("page", "0")
            .param("size", "10")
            .contentType(MediaType.APPLICATION_JSON));

        // Then: Should return only ACTIVE permits
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content[*].status").value(everyItem(equalTo("ACTIVE"))))
            .andExpect(jsonPath("$.totalElements").value(10)); // 30 permits, 1/3 are ACTIVE
    }

    @Test
    @DisplayName("GET /api/v1/permits/status/{status} - Should support custom sorting")
    void testGetPermitsByStatus_CustomSorting() throws Exception {
        // When: Request with ascending sort by permitNumber
        ResultActions result = mockMvc.perform(get("/api/v1/permits/status/COMPLETED")
            .param("page", "0")
            .param("size", "10")
            .param("sortBy", "permitNumber")
            .param("sortDir", "ASC")
            .contentType(MediaType.APPLICATION_JSON));

        // Then: Should return sorted results
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.content[*].status").value(everyItem(equalTo("COMPLETED"))))
            .andExpect(jsonPath("$.sort.sorted").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/permits/valid - Should return paginated valid permits")
    void testGetValidPermits_WithPagination() throws Exception {
        // When: Request valid permits
        ResultActions result = mockMvc.perform(get("/api/v1/permits/valid")
            .param("page", "0")
            .param("size", "10")
            .contentType(MediaType.APPLICATION_JSON));

        // Then: Should return paginated valid permits
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content.length()").value(lessThanOrEqualTo(10)))
            .andExpect(jsonPath("$.pageable.pageNumber").value(0))
            .andExpect(jsonPath("$.pageable.pageSize").value(10));
    }

    @Test
    @DisplayName("GET /api/v1/permits/valid - Should use default parameters")
    void testGetValidPermits_DefaultParameters() throws Exception {
        // When: Request without parameters
        ResultActions result = mockMvc.perform(get("/api/v1/permits/valid")
            .contentType(MediaType.APPLICATION_JSON));

        // Then: Should use defaults
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.pageable.pageNumber").value(0))
            .andExpect(jsonPath("$.pageable.pageSize").value(20));
    }

    @Test
    @DisplayName("Pagination should navigate through multiple pages")
    void testPagination_MultiplePages() throws Exception {
        // Given: Small page size to create multiple pages
        UUID livestockId = testLivestock.getId();

        // When: Request page 2 with size 10
        ResultActions result = mockMvc.perform(get("/api/v1/permits/livestock/{livestockId}", livestockId)
            .param("page", "2")
            .param("size", "10")
            .contentType(MediaType.APPLICATION_JSON));

        // Then: Should return third page (page 2, 0-indexed)
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content.length()").value(10))
            .andExpect(jsonPath("$.pageable.pageNumber").value(2))
            .andExpect(jsonPath("$.first").value(false))
            .andExpect(jsonPath("$.last").value(true));
    }

    @Test
    @DisplayName("Pagination should handle invalid status gracefully")
    void testGetPermitsByStatus_InvalidStatus() throws Exception {
        // When: Request with invalid status
        ResultActions result = mockMvc.perform(get("/api/v1/permits/status/INVALID_STATUS")
            .param("page", "0")
            .param("size", "10")
            .contentType(MediaType.APPLICATION_JSON));

        // Then: Should return 400 Bad Request
        result.andExpect(status().isBadRequest());
    }
}
