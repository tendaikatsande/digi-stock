package zw.co.digistock.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import zw.co.digistock.domain.Owner;
import zw.co.digistock.repository.OwnerRepository;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for OwnerController pagination
 */
@DisplayName("Owner Controller Integration Tests - Pagination")
class OwnerControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private OwnerRepository ownerRepository;

    private List<Owner> testOwners;

    @BeforeEach
    void setUp() {
        // Clean up
        ownerRepository.deleteAll();

        // Create test owners
        testOwners = new ArrayList<>();
        String[] districts = {"Harare", "Bulawayo", "Mutare", "Gweru", "Harare"};
        String[] provinces = {"Harare", "Bulawayo", "Manicaland", "Midlands", "Harare"};

        for (int i = 0; i < 35; i++) {
            Owner owner = Owner.builder()
                .nationalId(String.format("63-%06dA%02d", i + 100000, i % 100))
                .firstName(String.format("FirstName%d", i))
                .lastName(String.format("LastName%d", i))
                .phoneNumber(String.format("+26377%07d", 1000000 + i))
                .email(String.format("owner%d@example.com", i))
                .district(districts[i % 5])
                .province(provinces[i % 5])
                .address(String.format("%d Test Street", i + 1))
                .build();
            testOwners.add(ownerRepository.save(owner));
        }
    }

    @Test
    @DisplayName("GET /api/v1/owners - Should return paginated owners")
    void testGetAllOwners_WithPagination() throws Exception {
        // When: Request first page
        ResultActions result = mockMvc.perform(get("/api/v1/owners")
            .param("page", "0")
            .param("size", "20")
            .contentType(MediaType.APPLICATION_JSON));

        // Then: Should return paginated results
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content.length()").value(20))
            .andExpect(jsonPath("$.totalElements").value(35))
            .andExpect(jsonPath("$.totalPages").value(2))
            .andExpect(jsonPath("$.pageable.pageNumber").value(0))
            .andExpect(jsonPath("$.first").value(true))
            .andExpect(jsonPath("$.last").value(false));
    }

    @Test
    @DisplayName("GET /api/v1/owners - Should use default parameters")
    void testGetAllOwners_DefaultParameters() throws Exception {
        // When: Request without parameters
        ResultActions result = mockMvc.perform(get("/api/v1/owners")
            .contentType(MediaType.APPLICATION_JSON));

        // Then: Should use defaults (page=0, size=20, sortBy=createdAt, sortDir=DESC)
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.pageable.pageNumber").value(0))
            .andExpect(jsonPath("$.pageable.pageSize").value(20))
            .andExpect(jsonPath("$.sort.sorted").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/owners - Should support custom page size")
    void testGetAllOwners_CustomPageSize() throws Exception {
        // When: Request with custom page size
        ResultActions result = mockMvc.perform(get("/api/v1/owners")
            .param("page", "0")
            .param("size", "10")
            .contentType(MediaType.APPLICATION_JSON));

        // Then: Should return 10 items per page
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(10))
            .andExpect(jsonPath("$.pageable.pageSize").value(10))
            .andExpect(jsonPath("$.totalPages").value(4));
    }

    @Test
    @DisplayName("GET /api/v1/owners - Should support custom sorting")
    void testGetAllOwners_CustomSorting() throws Exception {
        // When: Request with ascending sort by lastName
        ResultActions result = mockMvc.perform(get("/api/v1/owners")
            .param("page", "0")
            .param("size", "20")
            .param("sortBy", "lastName")
            .param("sortDir", "ASC")
            .contentType(MediaType.APPLICATION_JSON));

        // Then: Should return sorted results
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.sort.sorted").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/owners/district/{district} - Should filter by district with pagination")
    void testGetOwnersByDistrict_WithPagination() throws Exception {
        // When: Request owners from Harare district
        ResultActions result = mockMvc.perform(get("/api/v1/owners/district/Harare")
            .param("page", "0")
            .param("size", "10")
            .contentType(MediaType.APPLICATION_JSON));

        // Then: Should return only Harare owners
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content[*].district").value(everyItem(equalTo("Harare"))))
            .andExpect(jsonPath("$.totalElements").value(14)); // 35 owners, 2/5 are from Harare
    }

    @Test
    @DisplayName("GET /api/v1/owners/district/{district} - Should support pagination for filtered results")
    void testGetOwnersByDistrict_Pagination() throws Exception {
        // When: Request second page
        ResultActions result = mockMvc.perform(get("/api/v1/owners/district/Harare")
            .param("page", "1")
            .param("size", "10")
            .contentType(MediaType.APPLICATION_JSON));

        // Then: Should return second page
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(4)) // 14 total, 10 on first page, 4 on second
            .andExpect(jsonPath("$.pageable.pageNumber").value(1))
            .andExpect(jsonPath("$.first").value(false))
            .andExpect(jsonPath("$.last").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/owners/search - Should search owners by name with pagination")
    void testSearchOwners_WithPagination() throws Exception {
        // When: Search for owners with "FirstName1" in their name
        ResultActions result = mockMvc.perform(get("/api/v1/owners/search")
            .param("q", "FirstName1")
            .param("page", "0")
            .param("size", "10")
            .contentType(MediaType.APPLICATION_JSON));

        // Then: Should return matching owners
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content.length()").value(greaterThan(0)))
            .andExpect(jsonPath("$.pageable.pageNumber").value(0));
    }

    @Test
    @DisplayName("GET /api/v1/owners/search - Should handle empty search results")
    void testSearchOwners_EmptyResults() throws Exception {
        // When: Search for non-existent owner
        ResultActions result = mockMvc.perform(get("/api/v1/owners/search")
            .param("q", "NonExistentOwner")
            .param("page", "0")
            .param("size", "10")
            .contentType(MediaType.APPLICATION_JSON));

        // Then: Should return empty page
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isEmpty())
            .andExpect(jsonPath("$.totalElements").value(0))
            .andExpect(jsonPath("$.totalPages").value(0));
    }

    @Test
    @DisplayName("GET /api/v1/owners/search - Should support sorting on search results")
    void testSearchOwners_WithSorting() throws Exception {
        // When: Search with custom sorting
        ResultActions result = mockMvc.perform(get("/api/v1/owners/search")
            .param("q", "LastName")
            .param("page", "0")
            .param("size", "10")
            .param("sortBy", "firstName")
            .param("sortDir", "ASC")
            .contentType(MediaType.APPLICATION_JSON));

        // Then: Should return sorted search results
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.sort.sorted").value(true));
    }

    @Test
    @DisplayName("Pagination should navigate to last page correctly")
    void testPagination_LastPage() throws Exception {
        // When: Request last page with size 10 (35 items total = 4 pages)
        ResultActions result = mockMvc.perform(get("/api/v1/owners")
            .param("page", "3")
            .param("size", "10")
            .contentType(MediaType.APPLICATION_JSON));

        // Then: Should return last page with 5 items
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(5))
            .andExpect(jsonPath("$.pageable.pageNumber").value(3))
            .andExpect(jsonPath("$.first").value(false))
            .andExpect(jsonPath("$.last").value(true))
            .andExpect(jsonPath("$.totalPages").value(4));
    }

    @Test
    @DisplayName("Pagination should handle single page result set")
    void testPagination_SinglePage() throws Exception {
        // When: Request with page size larger than total elements
        ResultActions result = mockMvc.perform(get("/api/v1/owners")
            .param("page", "0")
            .param("size", "100")
            .contentType(MediaType.APPLICATION_JSON));

        // Then: Should return all items in single page
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(35))
            .andExpect(jsonPath("$.totalPages").value(1))
            .andExpect(jsonPath("$.first").value(true))
            .andExpect(jsonPath("$.last").value(true));
    }
}
