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
import statisticsservice.domain.dailyStats.entity.DailyStats;
import statisticsservice.domain.dailyStats.entity.DailyTopBoard;
import statisticsservice.domain.dailyStats.repository.DailyRevenueRepository;
import statisticsservice.domain.dailyStats.repository.DailyStatsRepository;
import statisticsservice.domain.dailyStats.repository.DailyTopBoardRepository;
import statisticsservice.global.dto.PageDto;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

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

        DailyRevenue dailyRevenue = dailyRevenueRepository.findByAccountId(accountId)
                .orElseThrow(() -> new NoSuchElementException("일일 정산금을 찾을 수 없습니다."));

        return DailyRevenueResponse.of(dailyRevenue, dailyVideoRevenueList);
    }

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

    public DailyTopBoardResponse findDailyTopBoard(LocalDate date) {

        DailyTopBoard dailyTopBoard = dailyTopBoardRepository.findByDate(date)
                .orElseThrow(() -> new NoSuchElementException("데이터가 존재하지 않습니다."));

        return DailyTopBoardResponse.of(dailyTopBoard);
    }

    public PageDto<DailyStatsIdResponse> findDailyStatsList(Pageable pageable, LocalDate date) {

        Page<DailyStatsIdResponse> dailyStatsPage = dailyStatsRepository.findDailyStatsList(pageable, date)
                .map(DailyStatsIdResponse::of);

        return new PageDto<>(dailyStatsPage);
    }
}
