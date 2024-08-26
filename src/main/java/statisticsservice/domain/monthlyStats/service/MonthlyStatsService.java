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
import statisticsservice.global.exception.BusinessLogicException;
import statisticsservice.global.exception.ExceptionCode;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MonthlyStatsService {

    private final MonthlyTopBoardsRepository monthlyTopBoardRepository;

    public MonthlyTopBoardResponse findMonthlyTopBoard(LocalDate date) {

        MonthlyTopBoard monthlyTopBoard = monthlyTopBoardRepository.findByDate(date)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.NOT_FOUND_TOPBOARD));

        return MonthlyTopBoardResponse.of(monthlyTopBoard);
    }
}
