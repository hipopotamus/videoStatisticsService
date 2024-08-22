package statisticsservice.domain.monthlyStats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import statisticsservice.domain.monthlyStats.entity.MonthlyTopBoard;
import statisticsservice.domain.weeklyStats.entity.WeeklyTopBoard;

import java.time.LocalDate;
import java.util.Optional;

public interface MonthlyTopBoardsRepository extends JpaRepository<MonthlyTopBoard, Long> {

    Optional<MonthlyTopBoard> findByDate(LocalDate date);
}
