package statisticsservice.domain.weeklyStats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import statisticsservice.domain.weeklyStats.entity.TopBoards;

public interface TopBoardsRepository extends JpaRepository<TopBoards, Long> {
}
