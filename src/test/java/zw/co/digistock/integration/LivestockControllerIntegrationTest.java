package zw.co.digistock.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import zw.co.digistock.domain.Livestock;
import zw.co.digistock.domain.Owner;
import zw.co.digistock.domain.enums.Gender;
import zw.co.digistock.repository.LivestockRepository;
import zw.co.digistock.repository.OwnerRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for LivestockController pagination
 */
@DisplayName("Livestock Controller Integration Tests - Pagination")
class LivestockControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private LivestockRepository livestockRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    private Owner testOwner1;
    private Owner testOwner2;
    private List<Livestock> testLivestock;
    private Livestock testMother;
    private Livestock testFather;

    @BeforeEach
    void setUp() {
        // Clean up
        livestockRepository.deleteAll();
        ownerRepository.deleteAll();

        // Create test owners
        testOwner1 = Owner.builder()
            .nationalId("63-345678C34")
            .firstName("Charlie")
            .lastName("Brown")
            .phoneNumber("+263775678901")
            .email("charlie.brown@example.com")
            .district("Mutare")
            .province("Manicaland")
            .address("789 Test Road")
            .build();
        testOwner1 = ownerRepository.save(testOwner1);

        testOwner2 = Owner.builder()
            .nationalId("63-456789D45")
            .firstName("Diana")
            .lastName("Prince")
            .phoneNumber("+263776789012")
            .email("diana.prince@example.com")
            .district("Gweru")
            .province("Midlands")
            .address("321 Test Lane")
            .build();
        testOwner2 = ownerRepository.save(testOwner2);

        // Create parent livestock
        testMother = Livestock.builder()
            .tagCode("ZW-MU-MOTHER01")
            .name("Mother Cow")
            .breed("Brahman")
            .sex("F")
            .birthDate(LocalDate.now().minusYears(5))
            .owner(testOwner1)
            .stolen(false)
            .build();
        testMother = livestockRepository.save(testMother);

        testFather = Livestock.builder()
            .tagCode("ZW-MU-FATHER01")
            .name("Father Bull")
            .breed("Brahman")
            .sex("M")
            .birthDate(LocalDate.now().minusYears(6))
            .owner(testOwner1)
            .stolen(false)
            .build();
        testFather = livestockRepository.save(testFather);

        // Create test livestock
        testLivestock = new ArrayList<>();
        String[] breeds = {"Brahman", "Angus", "Hereford", "Simmental", "Brahman"};

        for (int i = 0; i < 40; i++) {
            Owner owner = i < 25 ? testOwner1 : testOwner2;
            Livestock livestock = Livestock.builder()
                .tagCode(String.format("ZW-MU-%06d", i + 1))
                .name(String.format("Livestock%d", i))
                .breed(breeds[i % 5])
                .sex(i % 2 == 0 ? "F" : "M")
                .birthDate(LocalDate.now().minusYears(i % 5))
                .owner(owner)
                .stolen(i % 10 == 0) // Every 10th is stolen
                .mother(i % 3 == 0 ? testMother : null)
                .father(i % 3 == 0 ? testFather : null)
                .build();
            testLivestock.add(livestockRepository.save(livestock));
        }
    }

    @Test
    @DisplayName("GET /api/v1/livestock/owner/{id} - Should return paginated livestock by owner")
    void testGetLivestockByOwner_WithPagination() throws Exception {
        // Given: Owner ID
        UUID ownerId = testOwner1.getId();

        // When: Request first page
        ResultActions result = mockMvc.perform(get("/api/v1/livestock/owner/{ownerId}", ownerId)
            .param("page", "0")
            .param("size", "20")
            .contentType(MediaType.APPLICATION_JSON));

        // Then: Should return paginated results
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content.length()").value(20))
            .andExpect(jsonPath("$.totalElements").value(27)) // 25 livestock + mother + father
            .andExpect(jsonPath("$.totalPages").value(2))
            .andExpect(jsonPath("$.pageable.pageNumber").value(0))
            .andExpect(jsonPath("$.content[*].owner.id").value(everyItem(equalTo(ownerId.toString()))));
    }

    @Test
    @DisplayName("GET /api/v1/livestock/breed/{breed} - Should filter by breed with pagination")
    void testGetLivestockByBreed_WithPagination() throws Exception {
        // When: Request Brahman breed
        ResultActions result = mockMvc.perform(get("/api/v1/livestock/breed/Brahman")
            .param("page", "0")
            .param("size", "10")
            .contentType(MediaType.APPLICATION_JSON));

        // Then: Should return only Brahman livestock
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content[*].breed").value(everyItem(equalTo("Brahman"))))
            .andExpect(jsonPath("$.pageable.pageSize").value(10));
    }

    @Test
    @DisplayName("GET /api/v1/livestock/{id}/offspring/mother - Should return paginated offspring by mother")
    void testGetOffspringByMother_WithPagination() throws Exception {
        // Given: Mother ID
        UUID motherId = testMother.getId();

        // When: Request offspring
        ResultActions result = mockMvc.perform(get("/api/v1/livestock/{id}/offspring/mother", motherId)
            .param("page", "0")
            .param("size", "10")
            .contentType(MediaType.APPLICATION_JSON));

        // Then: Should return paginated offspring
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content.length()").value(greaterThan(0)))
            .andExpect(jsonPath("$.pageable.pageNumber").value(0));
    }

    @Test
    @DisplayName("GET /api/v1/livestock/{id}/offspring/father - Should return paginated offspring by father")
    void testGetOffspringByFather_WithPagination() throws Exception {
        // Given: Father ID
        UUID fatherId = testFather.getId();

        // When: Request offspring
        ResultActions result = mockMvc.perform(get("/api/v1/livestock/{id}/offspring/father", fatherId)
            .param("page", "0")
            .param("size", "10")
            .contentType(MediaType.APPLICATION_JSON));

        // Then: Should return paginated offspring
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content.length()").value(greaterThan(0)))
            .andExpect(jsonPath("$.pageable.pageNumber").value(0));
    }

    @Test
    @DisplayName("GET /api/v1/livestock/stolen - Should return paginated stolen livestock")
    void testGetStolenLivestock_WithPagination() throws Exception {
        // When: Request stolen livestock
        ResultActions result = mockMvc.perform(get("/api/v1/livestock/stolen")
            .param("page", "0")
            .param("size", "10")
            .contentType(MediaType.APPLICATION_JSON));

        // Then: Should return only stolen livestock
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content[*].stolen").value(everyItem(equalTo(true))))
            .andExpect(jsonPath("$.totalElements").value(4)); // Every 10th of 40 = 4
    }

    @Test
    @DisplayName("GET /api/v1/livestock - Should return all livestock with pagination")
    void testGetAllLivestock_WithPagination() throws Exception {
        // When: Request all livestock
        ResultActions result = mockMvc.perform(get("/api/v1/livestock")
            .param("page", "0")
            .param("size", "25")
            .contentType(MediaType.APPLICATION_JSON));

        // Then: Should return paginated results
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content.length()").value(25))
            .andExpect(jsonPath("$.totalElements").value(42)) // 40 + mother + father
            .andExpect(jsonPath("$.pageable.pageSize").value(25));
    }

    @Test
    @DisplayName("GET /api/v1/livestock - Should use default parameters")
    void testGetAllLivestock_DefaultParameters() throws Exception {
        // When: Request without parameters
        ResultActions result = mockMvc.perform(get("/api/v1/livestock")
            .contentType(MediaType.APPLICATION_JSON));

        // Then: Should use defaults
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.pageable.pageNumber").value(0))
            .andExpect(jsonPath("$.pageable.pageSize").value(20))
            .andExpect(jsonPath("$.sort.sorted").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/livestock - Should support custom sorting")
    void testGetAllLivestock_CustomSorting() throws Exception {
        // When: Request with custom sorting
        ResultActions result = mockMvc.perform(get("/api/v1/livestock")
            .param("page", "0")
            .param("size", "20")
            .param("sortBy", "tagCode")
            .param("sortDir", "ASC")
            .contentType(MediaType.APPLICATION_JSON));

        // Then: Should return sorted results
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.sort.sorted").value(true));
    }

    @Test
    @DisplayName("Pagination should handle second owner's livestock separately")
    void testGetLivestockByOwner_SecondOwner() throws Exception {
        // Given: Second owner ID
        UUID ownerId = testOwner2.getId();

        // When: Request livestock
        ResultActions result = mockMvc.perform(get("/api/v1/livestock/owner/{ownerId}", ownerId)
            .param("page", "0")
            .param("size", "20")
            .contentType(MediaType.APPLICATION_JSON));

        // Then: Should return only second owner's livestock
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.totalElements").value(15)) // 40 total - 25 for owner1 = 15 for owner2
            .andExpect(jsonPath("$.content[*].owner.id").value(everyItem(equalTo(ownerId.toString()))));
    }

    @Test
    @DisplayName("Pagination should navigate through breed pages correctly")
    void testGetLivestockByBreed_MultiplePage() throws Exception {
        // When: Request second page of Brahman livestock
        ResultActions result = mockMvc.perform(get("/api/v1/livestock/breed/Brahman")
            .param("page", "1")
            .param("size", "5")
            .contentType(MediaType.APPLICATION_JSON));

        // Then: Should return second page
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.pageable.pageNumber").value(1))
            .andExpect(jsonPath("$.first").value(false));
    }

    @Test
    @DisplayName("Pagination should handle empty breed results")
    void testGetLivestockByBreed_NonExistentBreed() throws Exception {
        // When: Request non-existent breed
        ResultActions result = mockMvc.perform(get("/api/v1/livestock/breed/NonExistentBreed")
            .param("page", "0")
            .param("size", "10")
            .contentType(MediaType.APPLICATION_JSON));

        // Then: Should return empty page
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isEmpty())
            .andExpect(jsonPath("$.totalElements").value(0));
    }
}
