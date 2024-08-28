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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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

    private final JobLauncher jobLauncher;
    private final JobRegistry jobRegistry;
    private final DailyStatsBatchService dailyStatsBatchService;
    private final MonthlyStatsBatchService monthlyStatsBatchService;
    private final WeeklyStatsBatchService weeklyStatsBatchService;

    @PostMapping
    @Timed(value = "batch.daily")
    public ResponseEntity<String> generateBatch(@RequestParam LocalDate today) {

        JobParameters jobParameters = new JobParametersBuilder()
                .addLocalDate("date", today)
                .toJobParameters();

        try {
            dailyStats(today, jobParameters);
            weeklyStats(jobParameters, today);
            monthlyStats(jobParameters, today);
        } catch (Exception e) {
            throw new BusinessLogicException(ExceptionCode.FAIL_BATCH);
        }

        return new ResponseEntity<>("Success Batch", HttpStatus.OK);
    }

    private void dailyStats(LocalDate today, JobParameters jobParameters) throws Exception {

        JobExecution dailyStatsBatchJob = jobLauncher.run(jobRegistry.getJob("dailyStatsBatchJob"), jobParameters);
        if (dailyStatsBatchJob.getStatus() != BatchStatus.COMPLETED) {
            throw new BusinessLogicException(ExceptionCode.GLOBAL_EXCEPTION);
        }
        dailyStatsBatchService.addTopBoard(today);
    }

    private void weeklyStats(JobParameters jobParameters, LocalDate today) throws Exception {

        JobExecution weeklyStatsBatchJob = jobLauncher.run(jobRegistry.getJob("weeklyStatsBatchJob"), jobParameters);
        if (weeklyStatsBatchJob.getStatus() != BatchStatus.COMPLETED) {
            throw new BusinessLogicException(ExceptionCode.GLOBAL_EXCEPTION);
        }
        weeklyStatsBatchService.addTopBoard(today);
    }

    private void monthlyStats(JobParameters jobParameters, LocalDate today) throws Exception {

        JobExecution monthlyStatsBatchJob = jobLauncher.run(jobRegistry.getJob("monthlyStatsBatchJob"), jobParameters);
        if (monthlyStatsBatchJob.getStatus() != BatchStatus.COMPLETED) {
            throw new BusinessLogicException(ExceptionCode.GLOBAL_EXCEPTION);
        }
        monthlyStatsBatchService.addTopBoard(today);
    }
}
