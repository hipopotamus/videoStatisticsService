package statisticsservice.external.videoservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardStatisticListResponse {

    private Long accountId;

    private Long boardId;

    private long views;

    private long adViews;

    private long totalPlaytime;
}
