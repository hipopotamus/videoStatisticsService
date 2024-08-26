package statisticsservice.domain.weeklyStats.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import statisticsservice.domain.weeklyStats.dto.WeeklyTopBoardResponse;
import statisticsservice.domain.weeklyStats.entity.WeeklyTopBoard;
import statisticsservice.domain.weeklyStats.entity.WeeklyStats;
import statisticsservice.domain.weeklyStats.repository.WeeklyStatsRepository;
import statisticsservice.domain.weeklyStats.repository.WeeklyTopBoardsRepository;
import statisticsservice.global.exception.BusinessLogicException;
import statisticsservice.global.exception.ExceptionCode;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WeeklyStatsService {

    private final WeeklyTopBoardsRepository weeklyTopBoardsRepository;

    public WeeklyTopBoardResponse findWeeklyTopBoard(LocalDate date) {

        WeeklyTopBoard weeklyTopBoard = weeklyTopBoardsRepository.findByDate(date)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.NOT_FOUND_TOPBOARD));

        return WeeklyTopBoardResponse.of(weeklyTopBoard);
    }
}
