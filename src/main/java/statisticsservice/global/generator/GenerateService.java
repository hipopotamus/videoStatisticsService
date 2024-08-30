package statisticsservice.global.generator;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Service;
import statisticsservice.domain.dailyStats.batch.stock.DailyStatsBatchService;
import statisticsservice.domain.monthlyStats.batch.stock.MonthlyStatsBatchService;
import statisticsservice.domain.weeklyStats.batch.stock.WeeklyStatsBatchService;
import statisticsservice.global.exception.BusinessLogicException;
import statisticsservice.global.exception.ExceptionCode;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class GenerateService {

    private final JobLauncher jobLauncher;
    private final JobRegistry jobRegistry;
    private final DailyStatsBatchService dailyStatsBatchService;
    private final MonthlyStatsBatchService monthlyStatsBatchService;
    private final WeeklyStatsBatchService weeklyStatsBatchService;

    @Timed(value = "batch.dailyStats")
    public void dailyStats(LocalDate today, JobParameters jobParameters) throws Exception {

        JobExecution dailyStatsBatchJob = jobLauncher.run(jobRegistry.getJob("dailyStatsBatchJob"), jobParameters);
        if (dailyStatsBatchJob.getStatus() != BatchStatus.COMPLETED) {
            throw new BusinessLogicException(ExceptionCode.GLOBAL_EXCEPTION);
        }
        dailyStatsBatchService.addTopBoard(today);
    }

    @Timed(value = "batch.weeklyStats")
    public void weeklyStats(JobParameters jobParameters, LocalDate today) throws Exception {

        JobExecution weeklyStatsBatchJob = jobLauncher.run(jobRegistry.getJob("weeklyStatsBatchJob"), jobParameters);
        if (weeklyStatsBatchJob.getStatus() != BatchStatus.COMPLETED) {
            throw new BusinessLogicException(ExceptionCode.GLOBAL_EXCEPTION);
        }
        weeklyStatsBatchService.addTopBoard(today);
    }

    @Timed(value = "batch.monthlyStats")
    public void monthlyStats(JobParameters jobParameters, LocalDate today) throws Exception {

        JobExecution monthlyStatsBatchJob = jobLauncher.run(jobRegistry.getJob("monthlyStatsBatchJob"), jobParameters);
        if (monthlyStatsBatchJob.getStatus() != BatchStatus.COMPLETED) {
            throw new BusinessLogicException(ExceptionCode.GLOBAL_EXCEPTION);
        }
        monthlyStatsBatchService.addTopBoard(today);
    }
}
