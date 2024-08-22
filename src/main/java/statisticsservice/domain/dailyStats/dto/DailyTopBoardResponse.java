package statisticsservice.domain.dailyStats.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import statisticsservice.domain.dailyStats.entity.DailyTopBoard;
import statisticsservice.domain.weeklyStats.dto.WeeklyTopBoardResponse;
import statisticsservice.domain.weeklyStats.entity.WeeklyTopBoard;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DailyTopBoardResponse {

    private Long id;

    private LocalDate date;

    List<Long> boardIdListByViews = new ArrayList<>();

    List<Long> boardIdListByPlaytime = new ArrayList<>();

    public static DailyTopBoardResponse of(DailyTopBoard dailyTopBoard) {
        return DailyTopBoardResponse.builder()
                .id(dailyTopBoard.getId())
                .date(dailyTopBoard.getDate())
                .boardIdListByViews(dailyTopBoard.getBoardIdListByViews())
                .boardIdListByPlaytime(dailyTopBoard.getBoardIdListByPlaytime())
                .build();
    }
}
