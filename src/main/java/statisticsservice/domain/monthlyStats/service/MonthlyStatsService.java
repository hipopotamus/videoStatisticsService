package statisticsservice.domain.monthlyStats.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import statisticsservice.domain.monthlyStats.dto.MonthlyTopBoardResponse;
import statisticsservice.domain.monthlyStats.entity.MonthlyStats;
import statisticsservice.domain.monthlyStats.entity.MonthlyTopBoard;
import statisticsservice.domain.monthlyStats.repository.MonthlyStatsRepository;
import statisticsservice.domain.monthlyStats.repository.MonthlyTopBoardsRepository;
import statisticsservice.domain.weeklyStats.dto.WeeklyTopBoardResponse;
import statisticsservice.domain.weeklyStats.entity.WeeklyStats;
import statisticsservice.domain.weeklyStats.entity.WeeklyTopBoard;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MonthlyStatsService {

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

    public MonthlyTopBoardResponse findMonthlyTopBoard(LocalDate date) {

        MonthlyTopBoard monthlyTopBoard = monthlyTopBoardRepository.findByDate(date)
                .orElseThrow(() -> new NoSuchElementException("데이터가 존재하지 않습니다."));

        return MonthlyTopBoardResponse.of(monthlyTopBoard);
    }
}
