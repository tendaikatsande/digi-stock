package zw.co.digistock.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import zw.co.digistock.domain.District;
import zw.co.digistock.domain.Province;
import zw.co.digistock.domain.Ward;
import zw.co.digistock.repository.DistrictRepository;
import zw.co.digistock.repository.ProvinceRepository;
import zw.co.digistock.repository.WardRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Initializes the database with Zimbabwe's administrative divisions
 * (Provinces, Districts, and Wards)
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LocationDataInitializer implements CommandLineRunner {

    private final ProvinceRepository provinceRepository;
    private final DistrictRepository districtRepository;
    private final WardRepository wardRepository;

    @Override
    @Transactional
    public void run(String... args) {
        // Only initialize if no provinces exist
        if (provinceRepository.count() > 0) {
            log.info("Location data already initialized, skipping...");
            return;
        }

        log.info("Initializing Zimbabwe location data...");
        initializeLocationData();
        log.info("Location data initialization complete!");
    }

    private void initializeLocationData() {
        // Create all 10 provinces of Zimbabwe
        Province harare = createProvince("Harare", "HAR");
        Province bulawayo = createProvince("Bulawayo", "BUL");
        Province manicaland = createProvince("Manicaland", "MAN");
        Province mashonalandCentral = createProvince("Mashonaland Central", "MC");
        Province mashonalandEast = createProvince("Mashonaland East", "ME");
        Province mashonalandWest = createProvince("Mashonaland West", "MW");
        Province masvingo = createProvince("Masvingo", "MAV");
        Province matabelelandNorth = createProvince("Matabeleland North", "MN");
        Province matabelelandSouth = createProvince("Matabeleland South", "MS");
        Province midlands = createProvince("Midlands", "MID");

        // Save all provinces
        List<Province> provinces = List.of(
            harare, bulawayo, manicaland, mashonalandCentral, mashonalandEast,
            mashonalandWest, masvingo, matabelelandNorth, matabelelandSouth, midlands
        );
        provinceRepository.saveAll(provinces);

        // Create districts for each province
        // Harare Metropolitan
        createDistrictsAndWards(harare, new String[][]{
            {"Harare", "HAR-HAR", "Ward 1", "Ward 2", "Ward 3", "Ward 4", "Ward 5", "Ward 6", "Ward 7"},
            {"Chitungwiza", "HAR-CHI", "Ward 1", "Ward 2", "Ward 3", "Ward 4", "Ward 5"},
            {"Epworth", "HAR-EPW", "Ward 1", "Ward 2", "Ward 3"}
        });

        // Bulawayo Metropolitan
        createDistrictsAndWards(bulawayo, new String[][]{
            {"Bulawayo", "BUL-BUL", "Ward 1", "Ward 2", "Ward 3", "Ward 4", "Ward 5", "Ward 6", "Ward 7", "Ward 8", "Ward 9"}
        });

        // Manicaland
        createDistrictsAndWards(manicaland, new String[][]{
            {"Mutare", "MAN-MUT", "Ward 1", "Ward 2", "Ward 3", "Ward 4", "Ward 5"},
            {"Chimanimani", "MAN-CHM", "Ward 1", "Ward 2", "Ward 3", "Ward 4"},
            {"Chipinge", "MAN-CHP", "Ward 1", "Ward 2", "Ward 3", "Ward 4", "Ward 5", "Ward 6"},
            {"Makoni", "MAN-MAK", "Ward 1", "Ward 2", "Ward 3", "Ward 4"},
            {"Nyanga", "MAN-NYA", "Ward 1", "Ward 2", "Ward 3"},
            {"Buhera", "MAN-BUH", "Ward 1", "Ward 2", "Ward 3", "Ward 4", "Ward 5"},
            {"Mutasa", "MAN-MTA", "Ward 1", "Ward 2", "Ward 3"}
        });

        // Mashonaland Central
        createDistrictsAndWards(mashonalandCentral, new String[][]{
            {"Bindura", "MC-BIN", "Ward 1", "Ward 2", "Ward 3", "Ward 4"},
            {"Guruve", "MC-GUR", "Ward 1", "Ward 2", "Ward 3", "Ward 4", "Ward 5"},
            {"Centenary", "MC-CEN", "Ward 1", "Ward 2", "Ward 3"},
            {"Mazowe", "MC-MAZ", "Ward 1", "Ward 2", "Ward 3", "Ward 4"},
            {"Mount Darwin", "MC-MTD", "Ward 1", "Ward 2", "Ward 3", "Ward 4"},
            {"Rushinga", "MC-RUS", "Ward 1", "Ward 2", "Ward 3"},
            {"Shamva", "MC-SHA", "Ward 1", "Ward 2", "Ward 3", "Ward 4"}
        });

        // Mashonaland East
        createDistrictsAndWards(mashonalandEast, new String[][]{
            {"Marondera", "ME-MAR", "Ward 1", "Ward 2", "Ward 3", "Ward 4"},
            {"Chikomba", "ME-CHI", "Ward 1", "Ward 2", "Ward 3", "Ward 4"},
            {"Goromonzi", "ME-GOR", "Ward 1", "Ward 2", "Ward 3", "Ward 4"},
            {"Mudzi", "ME-MUD", "Ward 1", "Ward 2", "Ward 3"},
            {"Mutoko", "ME-MUT", "Ward 1", "Ward 2", "Ward 3", "Ward 4"},
            {"Seke", "ME-SEK", "Ward 1", "Ward 2", "Ward 3", "Ward 4", "Ward 5"},
            {"Wedza", "ME-WED", "Ward 1", "Ward 2", "Ward 3"},
            {"Hwedza", "ME-HWE", "Ward 1", "Ward 2", "Ward 3"},
            {"UMP", "ME-UMP", "Ward 1", "Ward 2", "Ward 3"}
        });

        // Mashonaland West
        createDistrictsAndWards(mashonalandWest, new String[][]{
            {"Chinhoyi", "MW-CHI", "Ward 1", "Ward 2", "Ward 3", "Ward 4"},
            {"Kariba", "MW-KAR", "Ward 1", "Ward 2", "Ward 3"},
            {"Makonde", "MW-MAK", "Ward 1", "Ward 2", "Ward 3", "Ward 4"},
            {"Zvimba", "MW-ZVI", "Ward 1", "Ward 2", "Ward 3", "Ward 4", "Ward 5"},
            {"Chegutu", "MW-CHE", "Ward 1", "Ward 2", "Ward 3", "Ward 4"},
            {"Hurungwe", "MW-HUR", "Ward 1", "Ward 2", "Ward 3", "Ward 4", "Ward 5"}
        });

        // Masvingo
        createDistrictsAndWards(masvingo, new String[][]{
            {"Masvingo", "MAV-MAS", "Ward 1", "Ward 2", "Ward 3", "Ward 4", "Ward 5"},
            {"Bikita", "MAV-BIK", "Ward 1", "Ward 2", "Ward 3", "Ward 4"},
            {"Chiredzi", "MAV-CHI", "Ward 1", "Ward 2", "Ward 3", "Ward 4", "Ward 5"},
            {"Chivi", "MAV-CHV", "Ward 1", "Ward 2", "Ward 3", "Ward 4"},
            {"Gutu", "MAV-GUT", "Ward 1", "Ward 2", "Ward 3", "Ward 4", "Ward 5"},
            {"Mwenezi", "MAV-MWE", "Ward 1", "Ward 2", "Ward 3", "Ward 4"},
            {"Zaka", "MAV-ZAK", "Ward 1", "Ward 2", "Ward 3", "Ward 4"}
        });

        // Matabeleland North
        createDistrictsAndWards(matabelelandNorth, new String[][]{
            {"Binga", "MN-BIN", "Ward 1", "Ward 2", "Ward 3", "Ward 4"},
            {"Bubi", "MN-BUB", "Ward 1", "Ward 2", "Ward 3"},
            {"Hwange", "MN-HWA", "Ward 1", "Ward 2", "Ward 3", "Ward 4", "Ward 5"},
            {"Lupane", "MN-LUP", "Ward 1", "Ward 2", "Ward 3", "Ward 4"},
            {"Nkayi", "MN-NKA", "Ward 1", "Ward 2", "Ward 3", "Ward 4"},
            {"Tsholotsho", "MN-TSH", "Ward 1", "Ward 2", "Ward 3", "Ward 4"},
            {"Umguza", "MN-UMG", "Ward 1", "Ward 2", "Ward 3"}
        });

        // Matabeleland South
        createDistrictsAndWards(matabelelandSouth, new String[][]{
            {"Beitbridge", "MS-BEI", "Ward 1", "Ward 2", "Ward 3", "Ward 4"},
            {"Bulilima", "MS-BUL", "Ward 1", "Ward 2", "Ward 3", "Ward 4"},
            {"Gwanda", "MS-GWA", "Ward 1", "Ward 2", "Ward 3", "Ward 4"},
            {"Insiza", "MS-INS", "Ward 1", "Ward 2", "Ward 3", "Ward 4"},
            {"Matobo", "MS-MAT", "Ward 1", "Ward 2", "Ward 3"},
            {"Mangwe", "MS-MAN", "Ward 1", "Ward 2", "Ward 3"},
            {"Umzingwane", "MS-UMZ", "Ward 1", "Ward 2", "Ward 3"}
        });

        // Midlands
        createDistrictsAndWards(midlands, new String[][]{
            {"Gweru", "MID-GWE", "Ward 1", "Ward 2", "Ward 3", "Ward 4", "Ward 5"},
            {"Kwekwe", "MID-KWE", "Ward 1", "Ward 2", "Ward 3", "Ward 4"},
            {"Gokwe North", "MID-GKN", "Ward 1", "Ward 2", "Ward 3", "Ward 4", "Ward 5"},
            {"Gokwe South", "MID-GKS", "Ward 1", "Ward 2", "Ward 3", "Ward 4"},
            {"Chirumhanzu", "MID-CHR", "Ward 1", "Ward 2", "Ward 3"},
            {"Mberengwa", "MID-MBE", "Ward 1", "Ward 2", "Ward 3", "Ward 4"},
            {"Shurugwi", "MID-SHU", "Ward 1", "Ward 2", "Ward 3"},
            {"Zvishavane", "MID-ZVI", "Ward 1", "Ward 2", "Ward 3", "Ward 4"}
        });

        log.info("Created {} provinces, {} districts, {} wards",
            provinceRepository.count(),
            districtRepository.count(),
            wardRepository.count());
    }

    private Province createProvince(String name, String code) {
        return Province.builder()
            .name(name)
            .code(code)
            .active(true)
            .build();
    }

    private void createDistrictsAndWards(Province province, String[][] districtsData) {
        for (String[] districtData : districtsData) {
            String districtName = districtData[0];
            String districtCode = districtData[1];

            District district = District.builder()
                .name(districtName)
                .code(districtCode)
                .province(province)
                .active(true)
                .build();

            District savedDistrict = districtRepository.save(district);

            // Create wards for this district (starting from index 2)
            List<Ward> wards = new ArrayList<>();
            for (int i = 2; i < districtData.length; i++) {
                String wardName = districtData[i];
                String wardCode = districtCode + "-" + wardName.replace(" ", "");

                Ward ward = Ward.builder()
                    .name(wardName)
                    .code(wardCode)
                    .district(savedDistrict)
                    .active(true)
                    .build();

                wards.add(ward);
            }

            if (!wards.isEmpty()) {
                wardRepository.saveAll(wards);
            }
        }
    }
}
