package statisticsservice.domain.dailyStats.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import statisticsservice.domain.dailyStats.entity.DailyStats;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DailyStatsIdResponse {

    private Long accountId;

    private Long boardId;

    public static DailyStatsIdResponse of(DailyStats dailyStats) {
        return DailyStatsIdResponse.builder()
                .accountId(dailyStats.getAccountId())
                .boardId(dailyStats.getBoardId())
                .build();
    }
}
