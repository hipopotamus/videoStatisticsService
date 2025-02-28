package statisticsservice.domain.dailyStats.entity;

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
public class DailyStats {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "dailyStats_id")
    private Long id;

    private Long accountId;

    private Long boardId;

    private long views;

    private long totalViews;

    private long adViews;

    private long totalAdViews;

    private long playtime;

    private long totalPlaytime;

    private double videoRevenue;

    private double adVideoRevenue;

    private double revenue;

    private LocalDate date;
}
