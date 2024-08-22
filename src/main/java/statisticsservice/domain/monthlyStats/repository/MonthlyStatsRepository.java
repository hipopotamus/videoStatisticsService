package statisticsservice.domain.monthlyStats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import statisticsservice.domain.monthlyStats.entity.MonthlyStats;
import statisticsservice.domain.weeklyStats.entity.WeeklyStats;

import java.time.LocalDate;
import java.util.List;

public interface MonthlyStatsRepository extends JpaRepository<MonthlyStats, Long> {

    List<MonthlyStats> findTop5ByDateOrderByViewsDesc(LocalDate date);

    List<MonthlyStats> findTop5ByDateOrderByPlaytimeDesc(LocalDate date);
}
