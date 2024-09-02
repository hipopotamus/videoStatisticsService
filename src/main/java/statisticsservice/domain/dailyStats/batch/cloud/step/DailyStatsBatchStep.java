package statisticsservice.domain.dailyStats.batch.cloud.step;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.SynchronizedItemReader;
import org.springframework.batch.item.support.builder.SynchronizedItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import statisticsservice.domain.dailyStats.entity.DailyStats;
import statisticsservice.domain.dailyStats.repository.DailyStatsJdbcRepository;
import statisticsservice.domain.dailyStats.repository.DailyStatsRepository;
import statisticsservice.domain.statistics.service.RevenueService;
import statisticsservice.external.videoservice.client.VideoServiceClient;
import statisticsservice.external.videoservice.dto.BoardStatisticListResponse;
import statisticsservice.global.dto.PageDto;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Configuration
@RequiredArgsConstructor
public class DailyStatsBatchStep {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final VideoServiceClient videoServiceClient;
    private final DailyStatsRepository dailyStatsRepository;
    private final RevenueService revenueService;
    private final DailyStatsJdbcRepository dailyStatsJdbcRepository;

    @Bean
    public Step dailyStatsStep() {
        return new StepBuilder("dailyStatsBatchStep", jobRepository)
                .<BoardStatisticListResponse, DailyStats>chunk(100, platformTransactionManager)
                .reader(boardStatisticsItemReaderByCursorWithSynchro(null))
                .processor(boardStatisticsItemProcessor(null))
                .writer(dailyStatsItemWriterByJdbc())
                .taskExecutor(dailyStatsTaskExecutor())
                .build();
    }

    @Bean
    @StepScope
    public ItemReader<BoardStatisticListResponse> boardStatisticsItemReader(@Value("#{jobParameters['date']}") LocalDate date) {
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
    public ItemReader<BoardStatisticListResponse> boardStatisticsItemReaderByCursor(@Value("#{jobParameters['date']}") LocalDate date) {
        return new ItemReader<>() {

            private Iterator<BoardStatisticListResponse> currentIterator;
            private final int limit = 100;
            private long lastBoardId = 0;

            @Override
            public BoardStatisticListResponse read() {
                if (currentIterator == null || !currentIterator.hasNext()) {
                    List<BoardStatisticListResponse> responseList = videoServiceClient.boardStatisticsCursor(lastBoardId, limit);
                    if (responseList == null || responseList.isEmpty()) {
                        return null;
                    }
                    currentIterator = responseList.iterator();
                    lastBoardId = responseList.getLast().getBoardId();
                }

                return currentIterator.hasNext() ? currentIterator.next() : null;
            }
        };
    }

    @Bean
    @StepScope
    public SynchronizedItemReader<BoardStatisticListResponse> boardStatisticsItemReaderByCursorWithSynchro(@Value("#{jobParameters['date']}") LocalDate date) {
        ItemReader<BoardStatisticListResponse> itemReader = new ItemReader<>() {

            private Iterator<BoardStatisticListResponse> currentIterator;
            private final int limit = 100;
            private long lastBoardId = 0;

            @Override
            public BoardStatisticListResponse read() {
                if (currentIterator == null || !currentIterator.hasNext()) {
                    List<BoardStatisticListResponse> responseList = videoServiceClient.boardStatisticsCursor(lastBoardId, limit);
                    if (responseList == null || responseList.isEmpty()) {
                        return null;
                    }
                    currentIterator = responseList.iterator();
                    lastBoardId = responseList.getLast().getBoardId();
                }

                return currentIterator.hasNext() ? currentIterator.next() : null;
            }
        };

        return new SynchronizedItemReaderBuilder<BoardStatisticListResponse>()
                .delegate(itemReader)
                .build();
    }

    @Bean
    @StepScope
    public ItemProcessor<BoardStatisticListResponse, DailyStats> boardStatisticsItemProcessor(@Value("#{jobParameters['date']}") LocalDate date) {

        return item -> {

            long views = 0;
            long adViews = 0;
            long playtime = 0;

            Optional<DailyStats> optionalDailyStats =
                    dailyStatsRepository.findByBoardIdAndDate(item.getBoardId(), date.minusDays(1));

            if (optionalDailyStats.isEmpty()) {
                views = item.getViews();
                adViews = item.getAdViews();
                playtime = item.getTotalPlaytime();
            } else {
                DailyStats beforeDailyStats = optionalDailyStats.get();
                views = item.getViews() - beforeDailyStats.getTotalViews();
                adViews = item.getAdViews() - beforeDailyStats.getAdViews();
                playtime = item.getTotalPlaytime() - beforeDailyStats.getTotalPlaytime();
            }

            double videoRevenue = revenueService.calculateVideoRevenue(item.getViews(), views);
            double adVideoRevenue = revenueService.calculateAdVideoRevenue(item.getAdViews(), adViews);


            return DailyStats.builder()
                    .accountId(item.getAccountId())
                    .boardId(item.getBoardId())
                    .views(views)
                    .totalViews(item.getViews())
                    .adViews(adViews)
                    .totalAdViews(item.getAdViews())
                    .playtime(playtime)
                    .totalPlaytime(item.getTotalPlaytime())
                    .videoRevenue(videoRevenue)
                    .adVideoRevenue(adVideoRevenue)
                    .revenue(videoRevenue + adVideoRevenue)
                    .date(date)
                    .build();
        };
    }

    @Bean
    @StepScope
    public ItemWriter<DailyStats> dailyStatsItemWriter() {
        return dailyStatsRepository::saveAll;
    }

    @Bean
    @StepScope
    public ItemWriter<DailyStats> dailyStatsItemWriterByJdbc() {
        return chunk -> dailyStatsJdbcRepository.batchInsertDailyStats(chunk.getItems());
    }

    @Bean
    public TaskExecutor dailyStatsTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(4);
        taskExecutor.setMaxPoolSize(8);
        taskExecutor.setQueueCapacity(100);
        taskExecutor.afterPropertiesSet();
        return taskExecutor;
    }
}
