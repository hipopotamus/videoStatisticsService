package statisticsservice.domain.dailyStats.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import statisticsservice.domain.dailyStats.dto.DailyRevenueResponse;
import statisticsservice.domain.dailyStats.dto.DailyVideoRevenueResponse;
import statisticsservice.domain.dailyStats.entity.DailyRevenue;
import statisticsservice.domain.dailyStats.repository.DailyRevenueRepository;
import statisticsservice.domain.dailyStats.repository.DailyStatsRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DailyStatsService {

    private final DailyStatsRepository dailyStatsRepository;
    private final DailyRevenueRepository dailyRevenueRepository;

    public DailyRevenueResponse findDailyRevenue(Long accountId, LocalDate date) {

        List<DailyVideoRevenueResponse> dailyVideoRevenueList =
                dailyStatsRepository.findByAccountIdAndDate(accountId, date).stream()
                .map(DailyVideoRevenueResponse::of)
                .toList();

        DailyRevenue dailyRevenue = dailyRevenueRepository.findByAccountId(accountId)
                .orElseThrow(() -> new NoSuchElementException("일일 정산금을 찾을 수 없습니다."));

        return DailyRevenueResponse.of(dailyRevenue, dailyVideoRevenueList);
    }
}
