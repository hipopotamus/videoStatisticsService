package statisticsservice.domain.dailyStats.batch.cloud.step;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
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
import statisticsservice.domain.statistics.service.RevenueService;
import statisticsservice.external.videoservice.client.VideoServiceClient;
import statisticsservice.external.videoservice.dto.BoardStatisticListResponse;
import statisticsservice.global.dto.PageDto;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.Optional;

@Configuration
@RequiredArgsConstructor
public class DailyStatsBatchStep {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final VideoServiceClient videoServiceClient;
    private final DailyStatsRepository dailyStatsRepository;
    private final RevenueService revenueService;

    @Bean
    public Step dailyStatsStep() {
        return new StepBuilder("dailyStatsBatchStep", jobRepository)
                .<BoardStatisticListResponse, DailyStats>chunk(100, platformTransactionManager)
                .reader(boardStatisticsItemReader(null))
                .processor(boardStatisticsItemProcessor(null))
                .writer(dailyStatsItemWriter())
                .build();
    }

    @Bean
    @StepScope
    public ItemReader<BoardStatisticListResponse> boardStatisticsItemReader(@Value("#{jobParameters['date']}") LocalDate currentDate) {
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
    public ItemProcessor<BoardStatisticListResponse, DailyStats> boardStatisticsItemProcessor(@Value("#{jobParameters['date']}") LocalDate currentDate) {

        return item -> {

            long views = 0;
            long adViews = 0;
            long playtime = 0;

            Optional<DailyStats> optionalDailyStats =
                    dailyStatsRepository.findByBoardIdAndDate(item.getBoardId(), currentDate.minusDays(1));

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
                    .date(currentDate)
                    .build();
        };
    }

    @Bean
    @StepScope
    public ItemWriter<DailyStats> dailyStatsItemWriter() {
        return dailyStatsRepository::saveAll;
    }
}
