package statisticsservice.global.generator;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.cglib.core.Local;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import statisticsservice.domain.dailyStats.batch.stock.DailyStatsBatchService;
import statisticsservice.domain.dailyStats.service.DailyStatsService;
import statisticsservice.domain.monthlyStats.batch.stock.MonthlyStatsBatchService;
import statisticsservice.domain.monthlyStats.service.MonthlyStatsService;
import statisticsservice.domain.weeklyStats.batch.stock.WeeklyStatsBatchService;
import statisticsservice.domain.weeklyStats.service.WeeklyStatsService;
import statisticsservice.global.exception.BusinessLogicException;
import statisticsservice.global.exception.ExceptionCode;
import statisticsservice.global.schedule.BatchSchedule;

import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/generate")
@RequiredArgsConstructor
public class GenerateController {

    private final GenerateService generateService;

    @PostMapping
    @Timed(value = "batch.total")
    public ResponseEntity<String> generateBatch(@RequestParam LocalDate today) {

        JobParameters jobParameters = new JobParametersBuilder()
                .addLocalDate("date", today)
                .toJobParameters();

        try {
            generateService.dailyStats(today, jobParameters);
            generateService.weeklyStats(jobParameters, today);
            generateService.monthlyStats(jobParameters, today);
        } catch (Exception e) {
            throw new BusinessLogicException(ExceptionCode.FAIL_BATCH);
        }

        return new ResponseEntity<>("Success Batch", HttpStatus.OK);
    }

    @GetMapping
    public String test() {
        return "test";
    }
}
