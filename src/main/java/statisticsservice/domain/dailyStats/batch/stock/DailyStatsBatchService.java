package statisticsservice.domain.dailyStats.batch.stock;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import statisticsservice.domain.dailyStats.entity.DailyStats;
import statisticsservice.domain.dailyStats.entity.DailyTopBoard;
import statisticsservice.domain.dailyStats.repository.DailyStatsRepository;
import statisticsservice.domain.dailyStats.repository.DailyTopBoardRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DailyStatsBatchService {

    private final DailyStatsRepository dailyStatsRepository;
    private final DailyTopBoardRepository dailyTopBoardRepository;

    @Transactional
    public void addTopBoard(LocalDate date) {

        List<Long> topBoardListByViews = dailyStatsRepository.findTop5ByDateOrderByViewsDesc(date).stream()
                .map(DailyStats::getBoardId)
                .toList();
        List<Long> topBoardListByPlaytime = dailyStatsRepository.findTop5ByDateOrderByPlaytimeDesc(date).stream()
                .map(DailyStats::getBoardId)
                .toList();

        DailyTopBoard dailyTopBoard = DailyTopBoard.builder()
                .boardIdListByViews(topBoardListByViews)
                .boardIdListByPlaytime(topBoardListByPlaytime)
                .date(date)
                .build();

        dailyTopBoardRepository.save(dailyTopBoard);
    }
}
