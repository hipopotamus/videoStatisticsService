package statisticsservice.domain.dailyStats.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import statisticsservice.domain.dailyStats.entity.DailyRevenue;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DailyRevenueResponse {

    private Long accountId;

    private double revenue;

    private List<DailyVideoRevenueResponse> videoRevenueList = new ArrayList<>();

    public static DailyRevenueResponse of(DailyRevenue dailyRevenue, List<DailyVideoRevenueResponse> videoRevenueList) {
        return DailyRevenueResponse.builder()
                .accountId(dailyRevenue.getAccountId())
                .revenue(dailyRevenue.getRevenue())
                .videoRevenueList(videoRevenueList)
                .build();
    }
}
