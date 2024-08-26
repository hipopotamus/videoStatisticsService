package statisticsservice.domain.weeklyStats.batch.cloud.job;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import statisticsservice.domain.weeklyStats.batch.cloud.step.WeeklyStatsBatchStep;

@Configuration
@RequiredArgsConstructor
public class WeeklyStatsJob {

    private final JobRepository jobRepository;
    private final WeeklyStatsBatchStep weeklyStatsBatchStep;

    @Bean
    public Job weeklyStatsBatchJob() {
        return new JobBuilder("weeklyStatsBatchJob", jobRepository)
                .start(weeklyStatsBatchStep.weeklyStatsStep())
                .build();
    }
}
