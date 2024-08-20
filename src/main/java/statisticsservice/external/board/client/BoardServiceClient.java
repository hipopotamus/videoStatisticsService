package statisticsservice.external.board.client;

import statisticsservice.external.board.dto.BoardDetailsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "videoservice")
public interface BoardServiceClient {

    @GetMapping("/boards/{boardId}")
    public BoardDetailsResponse BoardDetails(@PathVariable("boardId") Long boardId);
}
