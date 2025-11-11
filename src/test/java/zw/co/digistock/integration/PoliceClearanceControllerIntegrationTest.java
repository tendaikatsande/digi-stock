package zw.co.digistock.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import zw.co.digistock.domain.Livestock;
import zw.co.digistock.domain.Officer;
import zw.co.digistock.domain.Owner;
import zw.co.digistock.domain.PoliceClearance;
import zw.co.digistock.domain.enums.ClearanceStatus;
import zw.co.digistock.domain.enums.Gender;
import zw.co.digistock.domain.enums.UserRole;
import zw.co.digistock.repository.LivestockRepository;
import zw.co.digistock.repository.OfficerRepository;
import zw.co.digistock.repository.OwnerRepository;
import zw.co.digistock.repository.PoliceClearanceRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for PoliceClearanceController pagination
 */
@DisplayName("Police Clearance Controller Integration Tests - Pagination")
class PoliceClearanceControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private PoliceClearanceRepository clearanceRepository;

    @Autowired
    private LivestockRepository livestockRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private OfficerRepository officerRepository;

    private Owner testOwner;
    private Livestock testLivestock;
    private Officer testOfficer;
    private List<PoliceClearance> testClearances;

    @BeforeEach
    void setUp() {
        // Clean up
        clearanceRepository.deleteAll();
        livestockRepository.deleteAll();
        ownerRepository.deleteAll();
        officerRepository.deleteAll();

        // Create test owner
        testOwner = Owner.builder()
            .nationalId("63-123456A12")
            .firstName("John")
            .lastName("Doe")
            .phoneNumber("+263771234567")
            .email("john.doe@example.com")
            .district("Harare")
            .province("Harare")
            .address("123 Test Street")
            .build();
        testOwner = ownerRepository.save(testOwner);

        // Create test officer
        testOfficer = Officer.builder()
            .officerCode("PO-001")
            .firstName("Jane")
            .lastName("Smith")
            .phoneNumber("+263772345678")
            .email("jane.smith@police.gov.zw")
            .role(UserRole.POLICE_OFFICER)
            .station("Central Police")
            .province("Harare")
            .build();
        testOfficer = officerRepository.save(testOfficer);

        // Create test livestock
        testLivestock = Livestock.builder()
            .tagCode("ZW-HR-001234")
            .name("Bessie")
            .breed("Brahman")
            .gender(Gender.FEMALE)
            .dateOfBirth(LocalDate.now().minusYears(2))
            .owner(testOwner)
            .stolen(false)
            .build();
        testLivestock = livestockRepository.save(testLivestock);

        // Create test clearances
        testClearances = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            PoliceClearance clearance = PoliceClearance.builder()
                .clearanceNumber(String.format("PC-HR-%06d", i + 1))
                .livestock(testLivestock)
                .owner(testOwner)
                .issuedBy(testOfficer)
                .status(i % 3 == 0 ? ClearanceStatus.PENDING : ClearanceStatus.APPROVED)
                .clearanceDate(LocalDateTime.now().minusDays(i))
                .expiryDate(LocalDate.now().plusDays(14 - i))
                .build();
            testClearances.add(clearanceRepository.save(clearance));
        }
    }

    @Test
    @DisplayName("GET /api/v1/clearances/livestock/{id}/valid - Should return paginated valid clearances")
    void testGetValidClearancesForLivestock_WithPagination() throws Exception {
        // Given: Default pagination parameters
        UUID livestockId = testLivestock.getId();

        // When: Request first page
        ResultActions result = mockMvc.perform(get("/api/v1/clearances/livestock/{livestockId}/valid", livestockId)
            .param("page", "0")
            .param("size", "10")
            .contentType(MediaType.APPLICATION_JSON));

        // Then: Should return paginated results
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content.length()").value(lessThanOrEqualTo(10)))
            .andExpect(jsonPath("$.pageable.pageNumber").value(0))
            .andExpect(jsonPath("$.pageable.pageSize").value(10))
            .andExpect(jsonPath("$.totalElements").exists())
            .andExpect(jsonPath("$.totalPages").exists())
            .andExpect(jsonPath("$.first").value(true))
            .andExpect(jsonPath("$.last").exists());
    }

    @Test
    @DisplayName("GET /api/v1/clearances/livestock/{id}/valid - Should support custom page size")
    void testGetValidClearancesForLivestock_CustomPageSize() throws Exception {
        // Given: Custom page size
        UUID livestockId = testLivestock.getId();

        // When: Request with size 5
        ResultActions result = mockMvc.perform(get("/api/v1/clearances/livestock/{livestockId}/valid", livestockId)
            .param("page", "0")
            .param("size", "5")
            .contentType(MediaType.APPLICATION_JSON));

        // Then: Should return 5 items per page
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(lessThanOrEqualTo(5)))
            .andExpect(jsonPath("$.pageable.pageSize").value(5));
    }

    @Test
    @DisplayName("GET /api/v1/clearances/livestock/{id}/valid - Should support sorting")
    void testGetValidClearancesForLivestock_WithSorting() throws Exception {
        // Given: Sorting parameters
        UUID livestockId = testLivestock.getId();

        // When: Request with ascending sort by clearanceDate
        ResultActions result = mockMvc.perform(get("/api/v1/clearances/livestock/{livestockId}/valid", livestockId)
            .param("page", "0")
            .param("size", "10")
            .param("sortBy", "clearanceDate")
            .param("sortDir", "ASC")
            .contentType(MediaType.APPLICATION_JSON));

        // Then: Should return sorted results
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.sort.sorted").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/clearances/owner/{id} - Should return paginated clearances by owner")
    void testGetClearancesByOwner_WithPagination() throws Exception {
        // Given: Owner ID
        UUID ownerId = testOwner.getId();

        // When: Request first page
        ResultActions result = mockMvc.perform(get("/api/v1/clearances/owner/{ownerId}", ownerId)
            .param("page", "0")
            .param("size", "20")
            .contentType(MediaType.APPLICATION_JSON));

        // Then: Should return paginated results
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content.length()").value(lessThanOrEqualTo(20)))
            .andExpect(jsonPath("$.totalElements").value(25))
            .andExpect(jsonPath("$.totalPages").value(2))
            .andExpect(jsonPath("$.pageable.pageNumber").value(0));
    }

    @Test
    @DisplayName("GET /api/v1/clearances/owner/{id} - Should navigate to second page")
    void testGetClearancesByOwner_SecondPage() throws Exception {
        // Given: Owner ID and second page request
        UUID ownerId = testOwner.getId();

        // When: Request second page
        ResultActions result = mockMvc.perform(get("/api/v1/clearances/owner/{ownerId}", ownerId)
            .param("page", "1")
            .param("size", "20")
            .contentType(MediaType.APPLICATION_JSON));

        // Then: Should return second page
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content.length()").value(5)) // 25 total, 20 on first page, 5 on second
            .andExpect(jsonPath("$.pageable.pageNumber").value(1))
            .andExpect(jsonPath("$.first").value(false))
            .andExpect(jsonPath("$.last").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/clearances/pending - Should return paginated pending clearances")
    void testGetPendingClearances_WithPagination() throws Exception {
        // When: Request pending clearances
        ResultActions result = mockMvc.perform(get("/api/v1/clearances/pending")
            .param("page", "0")
            .param("size", "10")
            .contentType(MediaType.APPLICATION_JSON));

        // Then: Should return paginated pending clearances
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content[*].status").value(everyItem(equalTo("PENDING"))))
            .andExpect(jsonPath("$.pageable.pageSize").value(10));
    }

    @Test
    @DisplayName("GET /api/v1/clearances/pending - Should use default parameters")
    void testGetPendingClearances_DefaultParameters() throws Exception {
        // When: Request without parameters
        ResultActions result = mockMvc.perform(get("/api/v1/clearances/pending")
            .contentType(MediaType.APPLICATION_JSON));

        // Then: Should use defaults (page=0, size=20, sortBy=clearanceDate, sortDir=DESC)
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.pageable.pageNumber").value(0))
            .andExpect(jsonPath("$.pageable.pageSize").value(20));
    }

    @Test
    @DisplayName("GET /api/v1/clearances/pending - Should sort by clearanceDate descending by default")
    void testGetPendingClearances_DefaultSorting() throws Exception {
        // When: Request without sort parameters
        ResultActions result = mockMvc.perform(get("/api/v1/clearances/pending")
            .param("page", "0")
            .param("size", "20")
            .contentType(MediaType.APPLICATION_JSON));

        // Then: Should be sorted by clearanceDate descending
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.sort.sorted").value(true));
    }

    @Test
    @DisplayName("Pagination should enforce max page size limit")
    void testPagination_MaxSizeLimit() throws Exception {
        // When: Request with size exceeding limit (max is 100)
        ResultActions result = mockMvc.perform(get("/api/v1/clearances/pending")
            .param("page", "0")
            .param("size", "200")
            .contentType(MediaType.APPLICATION_JSON));

        // Then: Should cap at max size (handled by Constants.validatePageSize or Spring config)
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.pageable.pageSize").value(lessThanOrEqualTo(100)));
    }

    @Test
    @DisplayName("Pagination should return empty page for out of range page number")
    void testPagination_OutOfRangePage() throws Exception {
        // When: Request page beyond total pages
        ResultActions result = mockMvc.perform(get("/api/v1/clearances/pending")
            .param("page", "100")
            .param("size", "20")
            .contentType(MediaType.APPLICATION_JSON));

        // Then: Should return empty page
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isEmpty())
            .andExpect(jsonPath("$.pageable.pageNumber").value(100))
            .andExpect(jsonPath("$.totalElements").exists());
    }
}
