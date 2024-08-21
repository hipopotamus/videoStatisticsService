package statisticsservice.domain.weeklyStats.contorller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import statisticsservice.domain.weeklyStats.dto.WeeklyTopBoardResponse;
import statisticsservice.domain.weeklyStats.service.WeeklyStatsService;

import java.time.LocalDate;

@RestController
@RequestMapping("/weeklyStats")
@RequiredArgsConstructor
public class WeeklyStatsController {

    private final WeeklyStatsService weeklyStatsService;

    @GetMapping("/topBoards")
    public ResponseEntity<WeeklyTopBoardResponse> weeklyTopBoardDetails(@RequestParam LocalDate date) {

        WeeklyTopBoardResponse weeklyTopBoardResponse = weeklyStatsService.findWeeklyTopBoard(date);

        return new ResponseEntity<>(weeklyTopBoardResponse, HttpStatus.OK);
    }
}
