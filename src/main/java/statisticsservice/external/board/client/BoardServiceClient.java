package statisticsservice.external.board.client;

import org.springframework.data.domain.Pageable;
import statisticsservice.external.board.dto.BoardDetailsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import statisticsservice.external.board.dto.BoardStatisticListResponse;
import statisticsservice.global.dto.PageDto;

@FeignClient(name = "videoservice")
public interface BoardServiceClient {

    @GetMapping("/boards/{boardId}")
    public BoardDetailsResponse BoardDetails(@PathVariable("boardId") Long boardId);

    @GetMapping("/boards/statistics")
    public PageDto<BoardStatisticListResponse> boardStatisticsList(Pageable pageable);
}
