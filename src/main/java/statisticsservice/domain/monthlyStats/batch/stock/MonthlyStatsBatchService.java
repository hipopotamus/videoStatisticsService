package statisticsservice.domain.monthlyStats.batch.stock;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import statisticsservice.domain.monthlyStats.entity.MonthlyStats;
import statisticsservice.domain.monthlyStats.entity.MonthlyTopBoard;
import statisticsservice.domain.monthlyStats.repository.MonthlyStatsRepository;
import statisticsservice.domain.monthlyStats.repository.MonthlyTopBoardsRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MonthlyStatsBatchService {

    private final MonthlyStatsRepository monthlyStatsRepository;
    private final MonthlyTopBoardsRepository monthlyTopBoardRepository;

    @Transactional
    public void addTopBoard(LocalDate date) {

        List<Long> topBoardListByViews = monthlyStatsRepository.findTop5ByDateOrderByViewsDesc(date).stream()
                .map(MonthlyStats::getBoardId)
                .toList();
        List<Long> topBoardListByPlaytime = monthlyStatsRepository.findTop5ByDateOrderByPlaytimeDesc(date).stream()
                .map(MonthlyStats::getBoardId)
                .toList();

        MonthlyTopBoard monthlyTopBoard = MonthlyTopBoard.builder()
                .boardIdListByViews(topBoardListByViews)
                .boardIdListByPlaytime(topBoardListByPlaytime)
                .date(date)
                .build();

        monthlyTopBoardRepository.save(monthlyTopBoard);
    }
}
