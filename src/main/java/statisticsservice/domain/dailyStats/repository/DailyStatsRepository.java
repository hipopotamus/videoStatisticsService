package statisticsservice.domain.dailyStats.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import statisticsservice.domain.dailyStats.entity.DailyStats;
import statisticsservice.domain.weeklyStats.entity.WeeklyStats;

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
    List<DailyStats> findWeeklyData(@Param("boardId") Long boardId, @Param("start") LocalDate start, @Param("end") LocalDate end);

    Page<DailyStats> findDailyStatsList(Pageable pageable);

    List<DailyStats> findTop5ByDateOrderByViewsDesc(LocalDate date);

    List<DailyStats> findTop5ByDateOrderByPlaytimeDesc(LocalDate date);
}
