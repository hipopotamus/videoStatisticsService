package statisticsservice.domain.monthlyStats.batch.cloud.job;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import statisticsservice.domain.monthlyStats.batch.cloud.step.MonthlyStatsBatchStep;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class MonthlyStatsJob {

    private final JobRepository jobRepository;
    private final MonthlyStatsBatchStep monthlyStatsBatchStep;

    @Bean
    public Job monthlyStatsBatchJob(DataSource dataSource) {
        return new JobBuilder("monthlyStatsBatchJob", jobRepository)
                .start(monthlyStatsBatchStep.monthlyStatsStep(dataSource))
                .build();
    }
}

