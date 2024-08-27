package statisticsservice.domain.dailyStats.entity;

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
public class DailyRevenue {

    @Id
    @GeneratedValue
    @Column(name = "dailyRevenue_id")
    private Long id;

    private Long accountId;

    private LocalDate date;

    private double revenue;
}
