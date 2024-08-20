package statisticsservice.domain.dailyStats.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.PlatformTransactionManager;
import statisticsservice.domain.dailyStats.entity.DailStats;
import statisticsservice.domain.dailyStats.repository.DailyStatsRepository;
import statisticsservice.external.board.client.BoardServiceClient;
import statisticsservice.external.board.dto.BoardStatisticListResponse;
import statisticsservice.global.dto.PageDto;

import java.util.Iterator;

@Configuration
@RequiredArgsConstructor
public class DailyStatsBatch {

    private final JobRepository jobRepository;

    private final PlatformTransactionManager platformTransactionManager;

    private final BoardServiceClient boardServiceClient;

    private final DailyStatsRepository dailyStatsRepository;

    @Bean
    public Job dailyStatsBatchJob() {
        return new JobBuilder("dailyStatsBatchJob", jobRepository)
                .start(dailyStatsBatchStep())
                .build();
    }

    @Bean
    public Step dailyStatsBatchStep() {
        return new StepBuilder("dailyStatsBatchStep", jobRepository)
                .<BoardStatisticListResponse, DailStats>chunk(100, platformTransactionManager)
                .reader(boardStatisticsItemReader())
                .processor(boardStatisticsItemProcessor())
                .writer(dailyStatsItemWriter())
                .build();
    }

    @Bean
    @StepScope
    public ItemReader<BoardStatisticListResponse> boardStatisticsItemReader() {
        return new ItemReader<>() {

            private Iterator<BoardStatisticListResponse> currentIterator;
            private int currentPage = 1;

            @Override
            public BoardStatisticListResponse read() {
                if (currentIterator == null || !currentIterator.hasNext()) {
                    PageDto<BoardStatisticListResponse> page = boardServiceClient.boardStatisticsList(PageRequest.of(currentPage, 100));
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
    public ItemProcessor<BoardStatisticListResponse, DailStats> boardStatisticsItemProcessor() {
        return item -> DailStats.builder()
                .boardId(item.getBoardId())
                .totalViews(item.getViews())
                .totalPlaytime(item.getTotalPlaytime())
                .build();
    }

    @Bean
    @StepScope
    public ItemWriter<DailStats> dailyStatsItemWriter() {
        return dailyStatsRepository::saveAll;
    }
}
