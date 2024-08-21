package statisticsservice.domain.weeklyStats.dto;

import jakarta.persistence.Convert;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import statisticsservice.domain.weeklyStats.entity.WeeklyTopBoard;
import statisticsservice.global.jpa.converter.StringLongListConverter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeeklyTopBoardResponse {

    private Long id;

    private LocalDate date;

    List<Long> boardIdListByViews = new ArrayList<>();

    List<Long> boardIdListByPlaytime = new ArrayList<>();

    public static WeeklyTopBoardResponse of(WeeklyTopBoard weeklyTopBoard) {
        return WeeklyTopBoardResponse.builder()
                .id(weeklyTopBoard.getId())
                .date(weeklyTopBoard.getDate())
                .boardIdListByViews(weeklyTopBoard.getBoardIdListByViews())
                .boardIdListByPlaytime(weeklyTopBoard.getBoardIdListByPlaytime())
                .build();
    }
}
