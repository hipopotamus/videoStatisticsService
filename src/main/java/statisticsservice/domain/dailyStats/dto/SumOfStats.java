package statisticsservice.domain.dailyStats.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SumOfStats {

    private long totalViews;
    private long totalPlaytime;
}
