package statisticsservice.domain.dailyStats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import statisticsservice.domain.dailyStats.entity.DailyTopBoard;

import java.time.LocalDate;
import java.util.Optional;

public interface DailyTopBoardRepository extends JpaRepository<DailyTopBoard, Long> {

    Optional<DailyTopBoard> findByDate(LocalDate date);
}
