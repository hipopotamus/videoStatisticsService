package statisticsservice.global.schedule;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import statisticsservice.domain.dailyStats.batch.stock.DailyStatsBatchService;
import statisticsservice.domain.monthlyStats.batch.stock.MonthlyStatsBatchService;
import statisticsservice.domain.weeklyStats.batch.stock.WeeklyStatsBatchService;
import statisticsservice.global.exception.BusinessLogicException;
import statisticsservice.global.exception.ExceptionCode;

import java.time.LocalDate;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class BatchSchedule {

    private final JobLauncher jobLauncher;
    private final JobRegistry jobRegistry;
    private final DailyStatsBatchService dailyStatsBatchService;
    private final MonthlyStatsBatchService monthlyStatsBatchService;
    private final WeeklyStatsBatchService weeklyStatsBatchService;

    @Scheduled(cron = "0 0 0 * * ?")  // 매일 12시 정각에 실행
    public void runDailyStatsBatchJob() {

        LocalDate today = LocalDate.now();
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

