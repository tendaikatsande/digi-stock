package zw.co.digistock.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.co.digistock.domain.enums.ClearanceStatus;
import zw.co.digistock.domain.enums.PermitStatus;
import zw.co.digistock.dto.response.ClearanceAnalytics;
import zw.co.digistock.dto.response.DashboardSummary;
import zw.co.digistock.dto.response.LivestockAnalytics;
import zw.co.digistock.dto.response.PermitAnalytics;
import zw.co.digistock.repository.LivestockRepository;
import zw.co.digistock.repository.MovementPermitRepository;
import zw.co.digistock.repository.OwnerRepository;
import zw.co.digistock.repository.PoliceClearanceRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalyticsService {

    private final LivestockRepository livestockRepository;
    private final OwnerRepository ownerRepository;
    private final PoliceClearanceRepository clearanceRepository;
    private final MovementPermitRepository permitRepository;
    private final IVaccinationService vaccinationService;

    public DashboardSummary getDashboardSummary() {
        return DashboardSummary.builder()
                .livestock(getLivestockAnalytics())
                .permits(getPermitAnalytics())
                .clearances(getClearanceAnalytics())
                .vaccinations(vaccinationService.getVaccinationStatistics())
                .build();
    }

    public LivestockAnalytics getLivestockAnalytics() {
        long total = livestockRepository.count();
        long owners = ownerRepository.count();
        long stolen = livestockRepository.countByStolen(true);

        Map<String, Long> byProvince = toMap(livestockRepository.countGroupByProvince());
        Map<String, Long> byBreed = toMap(livestockRepository.countGroupByBreed());

        return LivestockAnalytics.builder()
                .totalLivestock(total)
                .totalOwners(owners)
                .stolenCount(stolen)
                .byProvince(byProvince)
                .byBreed(byBreed)
                .build();
    }

    public PermitAnalytics getPermitAnalytics() {
        long total = permitRepository.count();

        Map<String, Long> byStatus = new LinkedHashMap<>();
        for (PermitStatus status : PermitStatus.values()) {
            byStatus.put(status.name(), permitRepository.countByStatus(status));
        }

        LocalDate firstOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDateTime start = firstOfMonth.atStartOfDay();
        LocalDateTime end = firstOfMonth.plusMonths(1).atStartOfDay();
        long issuedThisMonth = permitRepository.countIssuedBetween(start, end);

        return PermitAnalytics.builder()
                .totalPermits(total)
                .byStatus(byStatus)
                .issuedThisMonth(issuedThisMonth)
                .build();
    }

    public ClearanceAnalytics getClearanceAnalytics() {
        long total = clearanceRepository.count();

        Map<String, Long> byStatus = new LinkedHashMap<>();
        for (ClearanceStatus status : ClearanceStatus.values()) {
            byStatus.put(status.name(), clearanceRepository.countByStatus(status));
        }

        return ClearanceAnalytics.builder()
                .totalClearances(total)
                .byStatus(byStatus)
                .build();
    }

    private Map<String, Long> toMap(List<Object[]> rows) {
        Map<String, Long> result = new LinkedHashMap<>();
        for (Object[] row : rows) {
            String key = row[0] != null ? row[0].toString() : "Unknown";
            Long value = ((Number) row[1]).longValue();
            result.put(key, value);
        }
        return result;
    }
}
