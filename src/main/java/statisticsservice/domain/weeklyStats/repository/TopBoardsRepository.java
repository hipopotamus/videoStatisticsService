package statisticsservice.domain.weeklyStats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import statisticsservice.domain.weeklyStats.entity.WeeklyTopBoard;

import java.time.LocalDate;
import java.util.Optional;

public interface TopBoardsRepository extends JpaRepository<WeeklyTopBoard, Long> {

    Optional<WeeklyTopBoard> findByDate(LocalDate date);
}
