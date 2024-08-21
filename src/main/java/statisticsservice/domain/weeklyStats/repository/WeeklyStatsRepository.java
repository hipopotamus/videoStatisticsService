package statisticsservice.domain.weeklyStats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import statisticsservice.domain.weeklyStats.entity.WeeklyStats;

public interface WeeklyStatsRepository extends JpaRepository<WeeklyStats, Long> {
}
