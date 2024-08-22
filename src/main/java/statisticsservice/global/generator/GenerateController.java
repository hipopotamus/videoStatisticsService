package statisticsservice.global.generator;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import statisticsservice.domain.dailyStats.service.DailyStatsService;
import statisticsservice.domain.weeklyStats.service.WeeklyStatsService;

import java.time.LocalDate;

@RestController
@RequestMapping("/generate")
@RequiredArgsConstructor
public class GenerateController {

    private final JobLauncher jobLauncher;
    private final JobRegistry jobRegistry;
    private final WeeklyStatsService weeklyStatsService;
    private final DailyStatsService dailyStatsService;

    @PostMapping("/daily")
    public ResponseEntity<String> generateDailyStats(@RequestParam LocalDate date) throws Exception {

        JobParameters jobParameters = new JobParametersBuilder()
                .addLocalDate("date", date)
                .toJobParameters();

        try {
            jobLauncher.run(jobRegistry.getJob("dailyStatsBatchJob"), jobParameters);
            dailyStatsService.addTopBoard(date);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>("Success Daily Batch", HttpStatus.OK);
    }

    @PostMapping("weekly")
    public ResponseEntity<String> generateWeeklyStats(@RequestParam LocalDate date) throws Exception {

        JobParameters jobParameters = new JobParametersBuilder()
                .addLocalDate("date", date)
                .toJobParameters();

        try {
            jobLauncher.run(jobRegistry.getJob("weeklyStatsBatchJob"), jobParameters);
            weeklyStatsService.addTopBoard(date);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>("Success Weekly Batch", HttpStatus.OK);
    }
}
