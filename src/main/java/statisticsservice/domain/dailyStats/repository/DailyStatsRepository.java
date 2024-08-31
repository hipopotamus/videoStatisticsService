package statisticsservice.domain.dailyStats.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import statisticsservice.domain.dailyStats.dto.SumOfStats;
import statisticsservice.domain.dailyStats.entity.DailyStats;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyStatsRepository extends JpaRepository<DailyStats, Long> {

    @Query("select dailyStats from DailyStats dailyStats " +
            "where dailyStats.boardId = :boardId " +
            "and dailyStats.date = :date")
    Optional<DailyStats> findByBoardIdAndDate(@Param("boardId") Long boardId, @Param("date") LocalDate date);

    @Query("select dailyStats from DailyStats dailyStats " +
            "where dailyStats.accountId = :accountId " +
            "and dailyStats.date = :date")
    List<DailyStats> findByAccountIdAndDate(@Param("accountId") Long accountId, @Param("date") LocalDate date);

    @Query("select dailyStats from DailyStats dailyStats where dailyStats.date between :start and :end and dailyStats.boardId = :boardId")
    List<DailyStats> findBetweenDates(@Param("boardId") Long boardId, @Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("select new statisticsservice.domain.dailyStats.dto.SumOfStats(SUM(dailyStats.views), SUM(dailyStats.playtime)) " +
            "from DailyStats dailyStats " +
            "where dailyStats.boardId = :boardId and dailyStats.date between :start and :end")
    SumOfStats findTotalViewsAndPlaytime(@Param("boardId") Long boardId,
                                         @Param("start") LocalDate start,
                                         @Param("end") LocalDate end);

    @Query("select dailyStats from DailyStats dailyStats where dailyStats.date = :date")
    Page<DailyStats> findDailyStatsList(Pageable pageable,@Param("date") LocalDate date);

    List<DailyStats> findTop5ByDateOrderByViewsDesc(LocalDate date);

    List<DailyStats> findTop5ByDateOrderByPlaytimeDesc(LocalDate date);
}
