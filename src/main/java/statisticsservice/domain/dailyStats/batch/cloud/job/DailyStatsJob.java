package statisticsservice.domain.dailyStats.batch.cloud.job;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import statisticsservice.domain.dailyStats.batch.cloud.step.DailyStatsBatchStep;
import statisticsservice.domain.dailyStats.batch.cloud.step.DailyRevenueBatchStep;

@Configuration
@RequiredArgsConstructor
public class DailyStatsJob {

    private final JobRepository jobRepository;
    private final DailyStatsBatchStep dailyStatsBatchStep;
    private final DailyRevenueBatchStep dailyRevenueBatchStep;

    @Bean
    @Timed(value = "batch.daily.stats")
    public Job dailyStatsBatchJob() {
        return new JobBuilder("dailyStatsBatchJob", jobRepository)
                .start(dailyStatsBatchStep.dailyStatsStep())
                .next(dailyRevenueBatchStep.dailyRevenueStep())
                .build();
    }
}
