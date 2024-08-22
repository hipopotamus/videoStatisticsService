package statisticsservice.domain.monthlyStats.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import statisticsservice.domain.monthlyStats.entity.MonthlyTopBoard;
import statisticsservice.domain.weeklyStats.entity.WeeklyTopBoard;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyTopBoardResponse {

    private Long id;

    private LocalDate date;

    List<Long> boardIdListByViews = new ArrayList<>();

    List<Long> boardIdListByPlaytime = new ArrayList<>();

    public static MonthlyTopBoardResponse of(MonthlyTopBoard monthlyTopBoard) {
        return MonthlyTopBoardResponse.builder()
                .id(monthlyTopBoard.getId())
                .date(monthlyTopBoard.getDate())
                .boardIdListByViews(monthlyTopBoard.getBoardIdListByViews())
                .boardIdListByPlaytime(monthlyTopBoard.getBoardIdListByPlaytime())
                .build();
    }
}
