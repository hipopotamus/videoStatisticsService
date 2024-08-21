package statisticsservice.domain.dailyStats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("select dailyStats from DailyStats dailyStats where dailyStats.date between :start and :end")
    List<DailyStats> findWeeklyData(@Param("start") LocalDate start, @Param("end") LocalDate end);
}
