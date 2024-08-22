package statisticsservice.domain.weeklyStats.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import statisticsservice.domain.weeklyStats.dto.WeeklyTopBoardResponse;
import statisticsservice.domain.weeklyStats.entity.WeeklyTopBoard;
import statisticsservice.domain.weeklyStats.entity.WeeklyStats;
import statisticsservice.domain.weeklyStats.repository.WeeklyStatsRepository;
import statisticsservice.domain.weeklyStats.repository.WeeklyTopBoardsRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WeeklyStatsService {

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

    public WeeklyTopBoardResponse findWeeklyTopBoard(LocalDate date) {

        WeeklyTopBoard weeklyTopBoard = weeklyTopBoardsRepository.findByDate(date)
                .orElseThrow(() -> new NoSuchElementException("데이터가 존재하지 않습니다."));

        return WeeklyTopBoardResponse.of(weeklyTopBoard);
    }
}
