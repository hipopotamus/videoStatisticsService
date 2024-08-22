package statisticsservice.domain.dailyStats.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import statisticsservice.domain.dailyStats.entity.DailyStats;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyVideoRevenueResponse {

    private Long boardId;

    private double totalRevenue;

    private double videoRevenue;

    private double adVideoRevenue;

    public static DailyVideoRevenueResponse of(DailyStats dailyStats) {
        return DailyVideoRevenueResponse.builder()
                .boardId(dailyStats.getBoardId())
                .totalRevenue(dailyStats.getRevenue())
                .videoRevenue(dailyStats.getVideoRevenue())
                .adVideoRevenue(dailyStats.getAdVideoRevenue())
                .build();
    }
}
