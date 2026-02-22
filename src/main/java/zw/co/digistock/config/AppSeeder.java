package zw.co.digistock.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import zw.co.digistock.domain.*;
import zw.co.digistock.domain.enums.UserRole;
import zw.co.digistock.repository.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Application data seeder for initializing the DigiStock system with default data.
 * Handles all location data (provinces, districts, wards), administrative users, and test data.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AppSeeder implements CommandLineRunner {

    private final ProvinceRepository provinceRepository;
    private final DistrictRepository districtRepository;
    private final WardRepository wardRepository;
    private final OfficerRepository officerRepository;
    private final OwnerRepository ownerRepository;
    private final LivestockRepository livestockRepository;
    private final VaccinationRepository vaccinationRepository;
    private final PasswordEncoder passwordEncoder;

    // Harare district and ward codes used for seeding officers/owners
    private static final String HARARE_PROVINCE_CODE  = "HAR";
    private static final String HARARE_DISTRICT_CODE  = "HAR-HAR";
    private static final String HARARE_WARD1_CODE     = "HAR-HAR-Ward1";

    @Override
    @Transactional
    public void run(String... args) {
        seedLocationData();
        seedAdministrativeUsers();
        seedTestData();
        log.info("DigiStock system seeded with initial data successfully!");
    }

    // -------------------------------------------------------------------------
    // Location data (replaces LocationDataInitializer)
    // -------------------------------------------------------------------------

    private void seedLocationData() {
        if (provinceRepository.count() > 0) {
            log.info("Location data already initialized, skipping...");
            return;
        }

        log.info("Initializing Zimbabwe location data...");

        Province harare             = createProvince("Harare",               HARARE_PROVINCE_CODE);
        Province bulawayo           = createProvince("Bulawayo",             "BUL");
        Province manicaland         = createProvince("Manicaland",           "MAN");
        Province mashonalandCentral = createProvince("Mashonaland Central",  "MC");
        Province mashonalandEast    = createProvince("Mashonaland East",     "ME");
        Province mashonalandWest    = createProvince("Mashonaland West",     "MW");
        Province masvingo           = createProvince("Masvingo",             "MAV");
        Province matabelelandNorth  = createProvince("Matabeleland North",   "MN");
        Province matabelelandSouth  = createProvince("Matabeleland South",   "MS");
        Province midlands           = createProvince("Midlands",             "MID");

        provinceRepository.saveAll(List.of(
            harare, bulawayo, manicaland, mashonalandCentral, mashonalandEast,
            mashonalandWest, masvingo, matabelelandNorth, matabelelandSouth, midlands
        ));

        createDistrictsAndWards(harare, new String[][]{
            {"Harare",       "HAR-HAR", "Ward 1", "Ward 2", "Ward 3", "Ward 4", "Ward 5", "Ward 6", "Ward 7"},
            {"Chitungwiza",  "HAR-CHI", "Ward 1", "Ward 2", "Ward 3", "Ward 4", "Ward 5"},
            {"Epworth",      "HAR-EPW", "Ward 1", "Ward 2", "Ward 3"}
        });

        createDistrictsAndWards(bulawayo, new String[][]{
            {"Bulawayo", "BUL-BUL", "Ward 1", "Ward 2", "Ward 3", "Ward 4", "Ward 5", "Ward 6", "Ward 7", "Ward 8", "Ward 9"}
        });

        createDistrictsAndWards(manicaland, new String[][]{
            {"Mutare",       "MAN-MUT", "Ward 1", "Ward 2", "Ward 3", "Ward 4", "Ward 5"},
            {"Chimanimani",  "MAN-CHM", "Ward 1", "Ward 2", "Ward 3", "Ward 4"},
            {"Chipinge",     "MAN-CHP", "Ward 1", "Ward 2", "Ward 3", "Ward 4", "Ward 5", "Ward 6"},
            {"Makoni",       "MAN-MAK", "Ward 1", "Ward 2", "Ward 3", "Ward 4"},
            {"Nyanga",       "MAN-NYA", "Ward 1", "Ward 2", "Ward 3"},
            {"Buhera",       "MAN-BUH", "Ward 1", "Ward 2", "Ward 3", "Ward 4", "Ward 5"},
            {"Mutasa",       "MAN-MTA", "Ward 1", "Ward 2", "Ward 3"}
        });

        createDistrictsAndWards(mashonalandCentral, new String[][]{
            {"Bindura",      "MC-BIN", "Ward 1", "Ward 2", "Ward 3", "Ward 4"},
            {"Guruve",       "MC-GUR", "Ward 1", "Ward 2", "Ward 3", "Ward 4", "Ward 5"},
            {"Centenary",    "MC-CEN", "Ward 1", "Ward 2", "Ward 3"},
            {"Mazowe",       "MC-MAZ", "Ward 1", "Ward 2", "Ward 3", "Ward 4"},
            {"Mount Darwin", "MC-MTD", "Ward 1", "Ward 2", "Ward 3", "Ward 4"},
            {"Rushinga",     "MC-RUS", "Ward 1", "Ward 2", "Ward 3"},
            {"Shamva",       "MC-SHA", "Ward 1", "Ward 2", "Ward 3", "Ward 4"}
        });

        createDistrictsAndWards(mashonalandEast, new String[][]{
            {"Marondera", "ME-MAR", "Ward 1", "Ward 2", "Ward 3", "Ward 4"},
            {"Chikomba",  "ME-CHI", "Ward 1", "Ward 2", "Ward 3", "Ward 4"},
            {"Goromonzi", "ME-GOR", "Ward 1", "Ward 2", "Ward 3", "Ward 4"},
            {"Mudzi",     "ME-MUD", "Ward 1", "Ward 2", "Ward 3"},
            {"Mutoko",    "ME-MUT", "Ward 1", "Ward 2", "Ward 3", "Ward 4"},
            {"Seke",      "ME-SEK", "Ward 1", "Ward 2", "Ward 3", "Ward 4", "Ward 5"},
            {"Wedza",     "ME-WED", "Ward 1", "Ward 2", "Ward 3"},
            {"Hwedza",    "ME-HWE", "Ward 1", "Ward 2", "Ward 3"},
            {"UMP",       "ME-UMP", "Ward 1", "Ward 2", "Ward 3"}
        });

        createDistrictsAndWards(mashonalandWest, new String[][]{
            {"Chinhoyi", "MW-CHI", "Ward 1", "Ward 2", "Ward 3", "Ward 4"},
            {"Kariba",   "MW-KAR", "Ward 1", "Ward 2", "Ward 3"},
            {"Makonde",  "MW-MAK", "Ward 1", "Ward 2", "Ward 3", "Ward 4"},
            {"Zvimba",   "MW-ZVI", "Ward 1", "Ward 2", "Ward 3", "Ward 4", "Ward 5"},
            {"Chegutu",  "MW-CHE", "Ward 1", "Ward 2", "Ward 3", "Ward 4"},
            {"Hurungwe", "MW-HUR", "Ward 1", "Ward 2", "Ward 3", "Ward 4", "Ward 5"}
        });

        createDistrictsAndWards(masvingo, new String[][]{
            {"Masvingo", "MAV-MAS", "Ward 1", "Ward 2", "Ward 3", "Ward 4", "Ward 5"},
            {"Bikita",   "MAV-BIK", "Ward 1", "Ward 2", "Ward 3", "Ward 4"},
            {"Chiredzi", "MAV-CHI", "Ward 1", "Ward 2", "Ward 3", "Ward 4", "Ward 5"},
            {"Chivi",    "MAV-CHV", "Ward 1", "Ward 2", "Ward 3", "Ward 4"},
            {"Gutu",     "MAV-GUT", "Ward 1", "Ward 2", "Ward 3", "Ward 4", "Ward 5"},
            {"Mwenezi",  "MAV-MWE", "Ward 1", "Ward 2", "Ward 3", "Ward 4"},
            {"Zaka",     "MAV-ZAK", "Ward 1", "Ward 2", "Ward 3", "Ward 4"}
        });

        createDistrictsAndWards(matabelelandNorth, new String[][]{
            {"Binga",       "MN-BIN", "Ward 1", "Ward 2", "Ward 3", "Ward 4"},
            {"Bubi",        "MN-BUB", "Ward 1", "Ward 2", "Ward 3"},
            {"Hwange",      "MN-HWA", "Ward 1", "Ward 2", "Ward 3", "Ward 4", "Ward 5"},
            {"Lupane",      "MN-LUP", "Ward 1", "Ward 2", "Ward 3", "Ward 4"},
            {"Nkayi",       "MN-NKA", "Ward 1", "Ward 2", "Ward 3", "Ward 4"},
            {"Tsholotsho",  "MN-TSH", "Ward 1", "Ward 2", "Ward 3", "Ward 4"},
            {"Umguza",      "MN-UMG", "Ward 1", "Ward 2", "Ward 3"}
        });

        createDistrictsAndWards(matabelelandSouth, new String[][]{
            {"Beitbridge",  "MS-BEI", "Ward 1", "Ward 2", "Ward 3", "Ward 4"},
            {"Bulilima",    "MS-BUL", "Ward 1", "Ward 2", "Ward 3", "Ward 4"},
            {"Gwanda",      "MS-GWA", "Ward 1", "Ward 2", "Ward 3", "Ward 4"},
            {"Insiza",      "MS-INS", "Ward 1", "Ward 2", "Ward 3", "Ward 4"},
            {"Matobo",      "MS-MAT", "Ward 1", "Ward 2", "Ward 3"},
            {"Mangwe",      "MS-MAN", "Ward 1", "Ward 2", "Ward 3"},
            {"Umzingwane",  "MS-UMZ", "Ward 1", "Ward 2", "Ward 3"}
        });

        createDistrictsAndWards(midlands, new String[][]{
            {"Gweru",        "MID-GWE", "Ward 1", "Ward 2", "Ward 3", "Ward 4", "Ward 5"},
            {"Kwekwe",       "MID-KWE", "Ward 1", "Ward 2", "Ward 3", "Ward 4"},
            {"Gokwe North",  "MID-GKN", "Ward 1", "Ward 2", "Ward 3", "Ward 4", "Ward 5"},
            {"Gokwe South",  "MID-GKS", "Ward 1", "Ward 2", "Ward 3", "Ward 4"},
            {"Chirumhanzu",  "MID-CHR", "Ward 1", "Ward 2", "Ward 3"},
            {"Mberengwa",    "MID-MBE", "Ward 1", "Ward 2", "Ward 3", "Ward 4"},
            {"Shurugwi",     "MID-SHU", "Ward 1", "Ward 2", "Ward 3"},
            {"Zvishavane",   "MID-ZVI", "Ward 1", "Ward 2", "Ward 3", "Ward 4"}
        });

        log.info("Created {} provinces, {} districts, {} wards",
            provinceRepository.count(), districtRepository.count(), wardRepository.count());
    }

    private Province createProvince(String name, String code) {
        return Province.builder().name(name).code(code).active(true).build();
    }

    private void createDistrictsAndWards(Province province, String[][] districtsData) {
        for (String[] districtData : districtsData) {
            District district = District.builder()
                .name(districtData[0])
                .code(districtData[1])
                .province(province)
                .active(true)
                .build();
            District saved = districtRepository.save(district);

            List<Ward> wards = new ArrayList<>();
            for (int i = 2; i < districtData.length; i++) {
                String wardName = districtData[i];
                wards.add(Ward.builder()
                    .name(wardName)
                    .code(districtData[1] + "-" + wardName.replace(" ", ""))
                    .district(saved)
                    .active(true)
                    .build());
            }
            if (!wards.isEmpty()) {
                wardRepository.saveAll(wards);
            }
        }
    }

    // -------------------------------------------------------------------------
    // Administrative users
    // -------------------------------------------------------------------------

    private void seedAdministrativeUsers() {
        if (!officerRepository.existsByOfficerCode("ADM001")) {
            Province harareProvince = provinceRepository.findByCode(HARARE_PROVINCE_CODE).orElseThrow();
            District harareCentral = districtRepository.findByProvinceAndCode(harareProvince, HARARE_DISTRICT_CODE).orElseThrow();
            Ward ward1 = wardRepository.findByDistrictAndCode(harareCentral, HARARE_WARD1_CODE).orElseThrow();

            // National Administrator
            Officer nationalAdmin = Officer.builder()
                    .firstName("John")
                    .lastName("Doe")
                    .email("national.admin@digistock.gov.zw")
                    .phoneNumber("0771234567")
                    .passwordHash(passwordEncoder.encode("Admin@2024"))
                    .role(UserRole.NATIONAL_ADMIN)
                    .province(harareProvince.getName())
                    .district(harareCentral.getName())
                    .ward(ward1.getName())
                    .officerCode("ADM001")
                    .active(true)
                    .build();
            officerRepository.save(nationalAdmin);

            // Provincial Administrator (Harare)
            Officer provincialAdmin = Officer.builder()
                    .firstName("Jane")
                    .lastName("Smith")
                    .email("provincial.admin.harare@digistock.gov.zw")
                    .phoneNumber("0771234568")
                    .passwordHash(passwordEncoder.encode("Admin@2024"))
                    .role(UserRole.PROVINCIAL_ADMIN)
                    .province(harareProvince.getName())
                    .district(harareCentral.getName())
                    .ward(ward1.getName())
                    .officerCode("ADM002")
                    .active(true)
                    .build();
            officerRepository.save(provincialAdmin);

            // District Administrator (Harare Central)
            Officer districtAdmin = Officer.builder()
                    .firstName("Michael")
                    .lastName("Johnson")
                    .email("district.admin.hararecentral@digistock.gov.zw")
                    .phoneNumber("0771234569")
                    .passwordHash(passwordEncoder.encode("Admin@2024"))
                    .role(UserRole.DISTRICT_ADMIN)
                    .province(harareProvince.getName())
                    .district(harareCentral.getName())
                    .ward(ward1.getName())
                    .officerCode("ADM003")
                    .active(true)
                    .build();
            officerRepository.save(districtAdmin);

            // AGRITEX Officer
            Officer agritexOfficer = Officer.builder()
                    .firstName("Sarah")
                    .lastName("Williams")
                    .email("agritex.officer@digistock.gov.zw")
                    .phoneNumber("0771234570")
                    .passwordHash(passwordEncoder.encode("Agritex@2024"))
                    .role(UserRole.AGRITEX_OFFICER)
                    .province(harareProvince.getName())
                    .district(harareCentral.getName())
                    .ward(ward1.getName())
                    .officerCode("AGR001")
                    .active(true)
                    .build();
            officerRepository.save(agritexOfficer);

            // Veterinary Officer
            Officer vetOfficer = Officer.builder()
                    .firstName("David")
                    .lastName("Brown")
                    .email("veterinary.officer@digistock.gov.zw")
                    .phoneNumber("0771234571")
                    .passwordHash(passwordEncoder.encode("Vet@2024"))
                    .role(UserRole.VETERINARY_OFFICER)
                    .province(harareProvince.getName())
                    .district(harareCentral.getName())
                    .ward(ward1.getName())
                    .officerCode("VET001")
                    .active(true)
                    .build();
            officerRepository.save(vetOfficer);

            // Police Officer
            Officer policeOfficer = Officer.builder()
                    .firstName("Robert")
                    .lastName("Wilson")
                    .email("police.officer@digistock.gov.zw")
                    .phoneNumber("0771234572")
                    .passwordHash(passwordEncoder.encode("Police@2024"))
                    .role(UserRole.POLICE_OFFICER)
                    .province(harareProvince.getName())
                    .district(harareCentral.getName())
                    .ward(ward1.getName())
                    .officerCode("POL001")
                    .active(true)
                    .build();
            officerRepository.save(policeOfficer);

            // System Administrator (legacy/system admin)
            Officer systemAdmin = Officer.builder()
                    .firstName("System")
                    .lastName("Administrator")
                    .email("admin@digistock.gov.zw")
                    .phoneNumber("0771234599")
                    .passwordHash(passwordEncoder.encode("Admin@2024"))
                    .role(UserRole.ADMIN)
                    .province(harareProvince.getName())
                    .district(harareCentral.getName())
                    .ward(ward1.getName())
                    .officerCode("SYSADMIN")
                    .active(true)
                    .build();
            officerRepository.save(systemAdmin);

            log.info("Administrative users seeded successfully");
        }
    }

    // -------------------------------------------------------------------------
    // Test data
    // -------------------------------------------------------------------------

    private void seedTestData() {
        if (ownerRepository.count() == 0) {
            Province harareProvince = provinceRepository.findByCode(HARARE_PROVINCE_CODE).orElseThrow();
            District harareCentral = districtRepository.findByProvinceAndCode(harareProvince, HARARE_DISTRICT_CODE).orElseThrow();
            Ward ward1 = wardRepository.findByDistrictAndCode(harareCentral, HARARE_WARD1_CODE).orElseThrow();

            // Test livestock owner
            Owner testOwner = Owner.builder()
                    .firstName("Tapiwa")
                    .lastName("Moyo")
                    .email("tapiwa.moyo@example.com")
                    .passwordHash(passwordEncoder.encode("Owner@2024"))
                    .role(UserRole.OWNER)
                    .active(true)
                    .phoneNumber("0771112223")
                    .nationalId("1234567890123")
                    .province(harareProvince.getName())
                    .district(harareCentral.getName())
                    .ward(ward1.getName())
                    .address("123 Main Street, Harare")
                    .build();
            ownerRepository.save(testOwner);

            // Test livestock animals
            List<Livestock> testLivestock = Arrays.asList(
                    Livestock.builder()
                            .tagCode("HR-01-01-12345-0001")
                            .name("Bull 001")
                            .breed("Angus")
                            .sex("Male")
                            .birthDate(LocalDate.of(2022, 5, 15))
                            .color("Black")
                            .owner(testOwner)
                            .build(),
                    Livestock.builder()
                            .tagCode("HR-01-01-12345-0002")
                            .name("Cow 002")
                            .breed("Brahman")
                            .sex("Female")
                            .birthDate(LocalDate.of(2023, 2, 10))
                            .color("Brown")
                            .owner(testOwner)
                            .build()
            );
            livestockRepository.saveAll(testLivestock);

            // Test vaccinations — only seed if the vet officer was seeded above
            Officer vetOfficer = officerRepository.findByOfficerCode("VET001").orElse(null);
            if (vetOfficer == null) {
                log.warn("VET001 officer not found — skipping vaccination seed data");
                return;
            }
            List<Vaccination> testVaccinations = Arrays.asList(
                    Vaccination.builder()
                            .vaccineType("Foot and Mouth Disease")
                            .vaccinationDate(LocalDate.of(2024, 1, 15))
                            .nextVaccinationDate(LocalDate.of(2025, 1, 15))
                            .batchNumber("FM202401")
                            .veterinaryOfficer(vetOfficer)
                            .livestock(testLivestock.get(0))
                            .build(),
                    Vaccination.builder()
                            .vaccineType("Anthrax")
                            .vaccinationDate(LocalDate.of(2024, 2, 20))
                            .nextVaccinationDate(LocalDate.of(2025, 2, 20))
                            .batchNumber("AN202402")
                            .veterinaryOfficer(vetOfficer)
                            .livestock(testLivestock.get(1))
                            .build()
            );
            vaccinationRepository.saveAll(testVaccinations);

            log.info("Test data seeded successfully");
        }
    }
}
