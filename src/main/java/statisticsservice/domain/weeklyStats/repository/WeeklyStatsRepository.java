package statisticsservice.domain.weeklyStats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import statisticsservice.domain.weeklyStats.entity.WeeklyStats;

import java.time.LocalDate;
import java.util.List;

public interface WeeklyStatsRepository extends JpaRepository<WeeklyStats, Long> {

    List<WeeklyStats> findTop5ByDateOrderByViewsDesc(LocalDate date);

    List<WeeklyStats> findTop5ByDateOrderByPlaytimeDesc(LocalDate date);
}
