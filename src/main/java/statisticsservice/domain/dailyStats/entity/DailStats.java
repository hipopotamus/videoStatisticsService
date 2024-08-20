package statisticsservice.domain.dailyStats.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DailStats {

    @Id
    @GeneratedValue
    @Column(name = "dailyStats_id")
    private Long id;

    private Long accountId;

    private Long boardId;

    private long views;

    private long adViews;

    private long totalViews;

    private long playtime;

    private long totalPlaytime;
}
