package statisticsservice.domain.dailyStats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import statisticsservice.domain.dailyStats.entity.DailStats;

public interface DailyStatsRepository extends JpaRepository<DailStats, Long> {
}
