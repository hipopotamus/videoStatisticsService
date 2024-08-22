package statisticsservice.domain.monthlyStats.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import statisticsservice.domain.monthlyStats.dto.MonthlyTopBoardResponse;
import statisticsservice.domain.monthlyStats.service.MonthlyStatsService;
import statisticsservice.domain.weeklyStats.dto.WeeklyTopBoardResponse;

import java.time.LocalDate;

@RestController
@RequestMapping("/monthlyStats")
@RequiredArgsConstructor
public class MonthlyStatsController {

    private final MonthlyStatsService monthlyStatsService;

    @GetMapping("/topBoards")
    public ResponseEntity<MonthlyTopBoardResponse> monthlyTopBoardDetails(@RequestParam LocalDate date) {

        MonthlyTopBoardResponse monthlyTopBoardResponse = monthlyStatsService.findMonthlyTopBoard(date);

        return new ResponseEntity<>(monthlyTopBoardResponse, HttpStatus.OK);
    }
}
