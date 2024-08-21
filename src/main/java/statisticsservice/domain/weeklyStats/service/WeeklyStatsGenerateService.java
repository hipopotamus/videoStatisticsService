package statisticsservice.domain.weeklyStats.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import statisticsservice.domain.weeklyStats.entity.TopBoards;
import statisticsservice.domain.weeklyStats.entity.WeeklyStats;
import statisticsservice.domain.weeklyStats.repository.TopBoardsRepository;
import statisticsservice.domain.weeklyStats.repository.WeeklyStatsRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WeeklyStatsGenerateService {

    private final WeeklyStatsRepository weeklyStatsRepository;
    private final TopBoardsRepository topBoardsRepository;

    public void generateTopBoards(LocalDate date) {

        List<Long> topBoardListByViews = weeklyStatsRepository.findTop5ByDateOrderByViewsDesc(date).stream()
                .map(WeeklyStats::getId)
                .toList();
        List<Long> topBoardListByPlaytime = weeklyStatsRepository.findTop5ByDateOrderByPlaytimeDesc(date).stream()
                .map(WeeklyStats::getId)
                .toList();

        TopBoards topBoards = TopBoards.builder()
                .boardIdListByViews(topBoardListByViews)
                .boardIdListByPlaytime(topBoardListByPlaytime)
                .date(date)
                .build();

        topBoardsRepository.save(topBoards);
    }
}
