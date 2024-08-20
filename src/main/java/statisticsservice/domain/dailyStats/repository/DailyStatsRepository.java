package statisticsservice.domain.dailyStats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import statisticsservice.domain.dailyStats.entity.DailyStats;

import java.time.LocalDate;
import java.util.Optional;

public interface DailyStatsRepository extends JpaRepository<DailyStats, Long> {

    @Query("select dailyStats from DailyStats dailyStats " +
            "where dailyStats.boardId = :boardId " +
            "and dailyStats.date = :date")
    Optional<DailyStats> findByIdAndDate(@Param("boardId") Long boardId, @Param("date") LocalDate date);
}
