package statisticsservice.domain.dailyStats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import statisticsservice.domain.dailyStats.entity.DailyRevenue;

import java.time.LocalDate;
import java.util.Optional;

public interface DailyRevenueRepository extends JpaRepository<DailyRevenue, Long> {

    Optional<DailyRevenue> findByAccountIdAndDate(Long accountId, LocalDate date);
}
