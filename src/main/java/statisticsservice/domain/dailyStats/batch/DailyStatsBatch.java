package statisticsservice.domain.dailyStats.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.PlatformTransactionManager;
import statisticsservice.domain.dailyStats.entity.DailyRevenue;
import statisticsservice.domain.dailyStats.entity.DailyStats;
import statisticsservice.domain.dailyStats.repository.DailyRevenueRepository;
import statisticsservice.domain.dailyStats.repository.DailyStatsRepository;
import statisticsservice.domain.statistics.service.RevenueService;
import statisticsservice.external.video.dto.AccountIdResponse;
import statisticsservice.external.video.client.VideoServiceClient;
import statisticsservice.external.video.dto.BoardStatisticListResponse;
import statisticsservice.global.dto.PageDto;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Configuration
@RequiredArgsConstructor
public class DailyStatsBatch {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final VideoServiceClient videoServiceClient;
    private final DailyStatsRepository dailyStatsRepository;
    private final DailyRevenueRepository dailyRevenueRepository;
    private final RevenueService revenueService;

    @Bean
    public Job dailyStatsBatchJob() {
        return new JobBuilder("dailyStatsBatchJob", jobRepository)
                .start(dailyStatsBatchStep())
                .next(dailyRevenueBatchStep())
                .build();
    }

    @Bean
    public Step dailyStatsBatchStep() {
        return new StepBuilder("dailyStatsBatchStep", jobRepository)
                .<BoardStatisticListResponse, DailyStats>chunk(100, platformTransactionManager)
                .reader(boardStatisticsItemReader(null))
                .processor(boardStatisticsItemProcessor(null))
                .writer(dailyStatsItemWriter())
                .build();
    }

    @Bean
    public Step dailyRevenueBatchStep() {
        return new StepBuilder("dailyRevenueBatchStep", jobRepository)
                .<AccountIdResponse, DailyRevenue>chunk(100, platformTransactionManager)
                .reader(accountIdItemReader(null))
                .processor(accountIdItemProcessor(null))
                .writer(dailyRevenueItemWriter())
                .build();
    }

    //**dailyStatsBatchStep
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
    //**dailyStatsBatchStep

    //**dailyRevenueBatchStep
    @Bean
    @StepScope
    public ItemReader<AccountIdResponse> accountIdItemReader(@Value("#{jobParameters['date']}") LocalDate currentDate) {
        return new ItemReader<>() {

            private Iterator<AccountIdResponse> currentIterator;
            private int currentPage = 1;

            @Override
            public AccountIdResponse read() {
                if (currentIterator == null || !currentIterator.hasNext()) {
                    PageDto<AccountIdResponse> page = videoServiceClient.accountList(PageRequest.of(currentPage, 100));
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
    public ItemProcessor<AccountIdResponse, DailyRevenue> accountIdItemProcessor(@Value("#{jobParameters['date']}") LocalDate currentDate) {
        return item -> {

            List<DailyStats> dailyStatsList = dailyStatsRepository.findByAccountIdAndDate(item.getAccountId(), currentDate);

            double totalRevenue = 0;
            for (DailyStats dailyStats : dailyStatsList) {
                totalRevenue += dailyStats.getRevenue();
            }

            return DailyRevenue.builder()
                    .accountId(item.getAccountId())
                    .data(currentDate)
                    .revenue(totalRevenue)
                    .build();
        };
    }

    @Bean
    @StepScope
    public ItemWriter<DailyRevenue> dailyRevenueItemWriter() {
        return dailyRevenueRepository::saveAll;
    }

    //**dailyRevenueBatchStep
}
