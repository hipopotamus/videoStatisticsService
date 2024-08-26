package statisticsservice.domain.weeklyStats.batch.stock;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import statisticsservice.domain.weeklyStats.entity.WeeklyStats;
import statisticsservice.domain.weeklyStats.entity.WeeklyTopBoard;
import statisticsservice.domain.weeklyStats.repository.WeeklyStatsRepository;
import statisticsservice.domain.weeklyStats.repository.WeeklyTopBoardsRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WeeklyStatsBatchService {

    private final WeeklyStatsRepository weeklyStatsRepository;
    private final WeeklyTopBoardsRepository weeklyTopBoardsRepository;

    @Transactional
    public void addTopBoard(LocalDate date) {

        List<Long> topBoardListByViews = weeklyStatsRepository.findTop5ByDateOrderByViewsDesc(date).stream()
                .map(WeeklyStats::getBoardId)
                .toList();
        List<Long> topBoardListByPlaytime = weeklyStatsRepository.findTop5ByDateOrderByPlaytimeDesc(date).stream()
                .map(WeeklyStats::getBoardId)
                .toList();

        WeeklyTopBoard weeklyTopBoard = WeeklyTopBoard.builder()
                .boardIdListByViews(topBoardListByViews)
                .boardIdListByPlaytime(topBoardListByPlaytime)
                .date(date)
                .build();

        weeklyTopBoardsRepository.save(weeklyTopBoard);
    }
}
