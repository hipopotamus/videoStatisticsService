package statisticsservice.domain.monthlyStats.entity;

import jakarta.persistence.*;
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
public class MonthlyStats {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "monthlyStats_id")
    private Long id;

    private Long accountId;

    private Long boardId;

    private long views;

    private long playtime;

    private LocalDate date;
}
