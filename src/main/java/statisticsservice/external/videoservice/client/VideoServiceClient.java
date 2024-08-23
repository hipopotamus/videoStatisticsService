package statisticsservice.external.videoservice.client;

import org.springframework.data.domain.Pageable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import statisticsservice.external.videoservice.dto.AccountIdResponse;
import statisticsservice.external.videoservice.dto.BoardStatisticListResponse;
import statisticsservice.global.dto.PageDto;

@FeignClient(name = "videoservice")
public interface VideoServiceClient {

    @GetMapping("/boards/statistics")
    public PageDto<BoardStatisticListResponse> boardStatisticsList(Pageable pageable);

    @GetMapping("/accounts")
    public PageDto<AccountIdResponse> accountList(Pageable pageable);
}
