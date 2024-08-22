package statisticsservice.domain.monthlyStats.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import statisticsservice.global.jpa.converter.StringLongListConverter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyTopBoard {

    @Id
    @GeneratedValue
    @Column(name = "monthlyTopBoard_id")
    private Long id;

    private LocalDate date;

    @Convert(converter = StringLongListConverter.class)
    List<Long> boardIdListByViews = new ArrayList<>();

    @Convert(converter = StringLongListConverter.class)
    List<Long> boardIdListByPlaytime = new ArrayList<>();
}
