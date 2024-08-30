package statisticsservice.external.videoservice.client;

import org.springframework.data.domain.Pageable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import statisticsservice.external.videoservice.dto.AccountIdResponse;
import statisticsservice.external.videoservice.dto.BoardStatisticListResponse;
import statisticsservice.global.dto.PageDto;

import java.util.List;

@FeignClient(name = "videoservice")
public interface VideoServiceClient {

    @GetMapping("/boards/statistics")
    public PageDto<BoardStatisticListResponse> boardStatisticsList(Pageable pageable);

    @GetMapping("/accounts")
    public PageDto<AccountIdResponse> accountList(Pageable pageable);

    @GetMapping("/boards/statistics/cursor")
    public List<BoardStatisticListResponse> boardStatisticsCursor(@RequestParam("lastBoardId") Long lastBoardId,
                                                                  @RequestParam("limit") int limit);

    @GetMapping("/accounts/cursor")
    public List<AccountIdResponse> accountIdCursor(
            @RequestParam("lastAccountId") Long lastAccountId, @RequestParam("limit") int limit);
}
