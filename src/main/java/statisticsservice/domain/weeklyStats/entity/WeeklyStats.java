package statisticsservice.domain.weeklyStats.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeeklyStats {

    @Id
    @GeneratedValue
    @Column(name = "weeklyStats_id")
    private Long id;

    private Long accountId;

    private Long boardId;

    private long views;

    private long playtime;

    private LocalDate date;
}
