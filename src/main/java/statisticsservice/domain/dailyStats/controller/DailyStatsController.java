package statisticsservice.domain.dailyStats.controller;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import statisticsservice.domain.dailyStats.dto.DailyRevenueResponse;
import statisticsservice.domain.dailyStats.dto.DailyTopBoardResponse;
import statisticsservice.domain.dailyStats.service.DailyStatsService;
import statisticsservice.domain.weeklyStats.dto.WeeklyTopBoardResponse;

import java.time.LocalDate;

@RestController
@RequestMapping("/dailyStats")
@RequiredArgsConstructor
public class DailyStatsController {

    private final DailyStatsService dailyStatsService;

    @GetMapping("/revenue/{accountId}")
    public ResponseEntity<DailyRevenueResponse> dailyRevenueDetails(@PathVariable Long accountId, @RequestParam LocalDate date) {

        DailyRevenueResponse dailyRevenue = dailyStatsService.findDailyRevenue(accountId, date);

        return new ResponseEntity<>(dailyRevenue, HttpStatus.OK);
    }

    @GetMapping("/topBoards")
    public ResponseEntity<DailyTopBoardResponse> dailyTopBoardDetails(@RequestParam LocalDate date) {

        DailyTopBoardResponse dailyTopBoardResponse = dailyStatsService.findDailyTopBoard(date);

        return new ResponseEntity<>(dailyTopBoardResponse, HttpStatus.OK);
    }
}
