package statisticsservice.domain.dailyStats.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import statisticsservice.domain.dailyStats.dto.DailyRevenueResponse;
import statisticsservice.domain.dailyStats.dto.DailyStatsIdResponse;
import statisticsservice.domain.dailyStats.dto.DailyTopBoardResponse;
import statisticsservice.domain.dailyStats.dto.DailyVideoRevenueResponse;
import statisticsservice.domain.dailyStats.entity.DailyRevenue;
import statisticsservice.domain.dailyStats.entity.DailyTopBoard;
import statisticsservice.domain.dailyStats.repository.DailyRevenueRepository;
import statisticsservice.domain.dailyStats.repository.DailyStatsRepository;
import statisticsservice.domain.dailyStats.repository.DailyTopBoardRepository;
import statisticsservice.global.dto.PageDto;
import statisticsservice.global.exception.BusinessLogicException;
import statisticsservice.global.exception.ExceptionCode;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DailyStatsService {

    private final DailyStatsRepository dailyStatsRepository;
    private final DailyRevenueRepository dailyRevenueRepository;
    private final DailyTopBoardRepository dailyTopBoardRepository;

    public DailyRevenueResponse findDailyRevenue(Long accountId, LocalDate date) {

        List<DailyVideoRevenueResponse> dailyVideoRevenueList =
                dailyStatsRepository.findByAccountIdAndDate(accountId, date).stream()
                .map(DailyVideoRevenueResponse::of)
                .toList();

        DailyRevenue dailyRevenue = dailyRevenueRepository.findByAccountIdAndDate(accountId, date)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.NOT_FOUND_REVENUE));

        return DailyRevenueResponse.of(dailyRevenue, dailyVideoRevenueList);
    }

    public DailyTopBoardResponse findDailyTopBoard(LocalDate date) {

        DailyTopBoard dailyTopBoard = dailyTopBoardRepository.findByDate(date)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.NOT_FOUND_TOPBOARD));

        return DailyTopBoardResponse.of(dailyTopBoard);
    }

    public PageDto<DailyStatsIdResponse> findDailyStatsList(Pageable pageable, LocalDate date) {

        Page<DailyStatsIdResponse> dailyStatsPage = dailyStatsRepository.findDailyStatsList(pageable, date)
                .map(DailyStatsIdResponse::of);

        return new PageDto<>(dailyStatsPage);
    }
}
