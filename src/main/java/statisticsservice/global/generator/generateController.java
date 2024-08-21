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
import statisticsservice.domain.weeklyStats.entity.WeeklyStats;
import statisticsservice.domain.weeklyStats.service.WeeklyStatsGenerateService;

import java.time.LocalDate;

@RestController
@RequestMapping("/generate")
@RequiredArgsConstructor
public class generateController {

    private final JobLauncher jobLauncher;
    private final JobRegistry jobRegistry;
    private final WeeklyStatsGenerateService weeklyStatsGenerateService;

    @PostMapping("/daily")
    public ResponseEntity<String> generateDailyStats(@RequestParam LocalDate date) throws Exception {

        JobParameters jobParameters = new JobParametersBuilder()
                .addLocalDate("date", date)
                .toJobParameters();

        jobLauncher.run(jobRegistry.getJob("dailyStatsBatchJob"), jobParameters);

        return new ResponseEntity<>("Success Daily Batch", HttpStatus.OK);
    }

    @PostMapping("weekly")
    public ResponseEntity<String> generateWeeklyStats(@RequestParam LocalDate date) throws Exception {

        JobParameters jobParameters = new JobParametersBuilder()
                .addLocalDate("date", date)
                .toJobParameters();

        jobLauncher.run(jobRegistry.getJob("weeklyStatsBatchJob"), jobParameters);
        weeklyStatsGenerateService.generateTopBoards(date);

        return new ResponseEntity<>("Success Weekly Batch", HttpStatus.OK);
    }
}
