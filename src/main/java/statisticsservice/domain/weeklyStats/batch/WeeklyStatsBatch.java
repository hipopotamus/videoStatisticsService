package statisticsservice.domain.weeklyStats.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.PlatformTransactionManager;
import statisticsservice.domain.dailyStats.entity.DailyStats;
import statisticsservice.domain.dailyStats.repository.DailyStatsRepository;
import statisticsservice.domain.weeklyStats.entity.WeeklyStats;
import statisticsservice.domain.weeklyStats.repository.WeeklyStatsRepository;
import statisticsservice.external.video.client.VideoServiceClient;
import statisticsservice.external.video.dto.BoardStatisticListResponse;
import statisticsservice.global.dto.PageDto;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Configuration
@RequiredArgsConstructor
public class WeeklyStatsBatch {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final DailyStatsRepository dailyStatsRepository;
    private final WeeklyStatsRepository weeklyStatsRepository;
    private final VideoServiceClient videoServiceClient;

    @Bean
    public Job weeklyStatsBatchJob() {
        return new JobBuilder("weeklyStatsBatchJob", jobRepository)
                .start(weeklyStatsBatchStep())
                .build();
    }

    @Bean
    public Step weeklyStatsBatchStep() {
        return new StepBuilder("weeklyStatsBatchStep", jobRepository)
                .<BoardStatisticListResponse, WeeklyStats>chunk(100, platformTransactionManager)
                .reader(boardIdItemReader(null))
                .processor(boardIdItemProcessor(null))
                .writer(weeklyStatsItemWriter())
                .build();
    }

    //**weeklyStatsBatchStep
    @Bean
    @StepScope
    public ItemReader<BoardStatisticListResponse> boardIdItemReader(@Value("#{jobParameters['date']}") LocalDate currentDate) {
        return new ItemReader<>() {

            private Iterator<BoardStatisticListResponse> currentIterator;
            private int currentPage = 1;

            @Override
            public BoardStatisticListResponse read() {
                if (currentIterator == null || !currentIterator.hasNext()) {
                    PageDto<BoardStatisticListResponse> page = videoServiceClient.boardStatisticsList(PageRequest.of(currentPage, 100));
                    if (page == null || page.getContent().isEmpty()) {
                        return null;
                    }
                    currentIterator = page.getContent().iterator();
                    currentPage++;
                }

                return currentIterator.hasNext() ? currentIterator.next() : null;
            }
        };
    }

    @Bean
    @StepScope
    public ItemProcessor<BoardStatisticListResponse, WeeklyStats> boardIdItemProcessor(@Value("#{jobParameters['date']}") LocalDate currentDate) {

        return item -> {

            long views = 0;
            long playtime = 0;

            LocalDate start = currentDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            List<DailyStats> weeklyDataList = dailyStatsRepository.findWeeklyData(start, currentDate);

            for (DailyStats dailyStats : weeklyDataList) {
                views += dailyStats.getViews();
                playtime += dailyStats.getPlaytime();
            }

            return WeeklyStats.builder()
                    .accountId(item.getAccountId())
                    .boardId(item.getBoardId())
                    .views(views)
                    .playtime(playtime)
                    .data(currentDate)
                    .build();
        };
    }

    @Bean
    @StepScope
    public ItemWriter<WeeklyStats> weeklyStatsItemWriter() {
        return weeklyStatsRepository::saveAll;
    }
    //**weeklyStatsBatchStep


}
