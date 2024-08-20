package statisticsservice.domain.dailyStats.controller;

import statisticsservice.external.board.client.BoardServiceClient;
import statisticsservice.external.board.dto.BoardDetailsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dailyStats")
@RequiredArgsConstructor
public class DailyStatsController {

    private final BoardServiceClient boardServiceClient;

    @GetMapping("/{boardId}")
    public ResponseEntity<BoardDetailsResponse> boardDetails(@PathVariable Long boardId) {

        BoardDetailsResponse boardDetailsResponse = boardServiceClient.BoardDetails(boardId);

        return new ResponseEntity<>(boardDetailsResponse, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<String> test() {
        return new ResponseEntity<>("Hello World!", HttpStatus.OK);
    }

}
