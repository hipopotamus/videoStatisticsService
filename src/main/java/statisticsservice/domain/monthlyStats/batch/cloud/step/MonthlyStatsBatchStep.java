package statisticsservice.domain.monthlyStats.batch.cloud.step;

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
import statisticsservice.domain.dailyStats.dto.DailyStatsIdResponse;
import statisticsservice.domain.dailyStats.entity.DailyStats;
import statisticsservice.domain.dailyStats.repository.DailyStatsRepository;
import statisticsservice.domain.dailyStats.service.DailyStatsService;
import statisticsservice.domain.monthlyStats.entity.MonthlyStats;
import statisticsservice.domain.monthlyStats.repository.MonthlyStatsRepository;
import statisticsservice.global.dto.PageDto;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Iterator;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class MonthlyStatsBatchStep {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final DailyStatsRepository dailyStatsRepository;
    private final DailyStatsService dailyStatsService;
    private final MonthlyStatsRepository monthlyStatsRepository;

    @Bean
    public Step monthlyStatsStep() {
        return new StepBuilder("monthlyStatsBatchStep", jobRepository)
                .<DailyStatsIdResponse, MonthlyStats>chunk(100, platformTransactionManager)
                .reader(monthlyItemReader(null))
                .processor(monthlyItemProcessor(null))
                .writer(montlyStatsItemWriter())
                .build();
    }

    @Bean
    @StepScope
    public ItemReader<DailyStatsIdResponse> monthlyItemReader(@Value("#{jobParameters['date']}") LocalDate date) {
        return new ItemReader<>() {

            private Iterator<DailyStatsIdResponse> currentIterator;
            private int currentPage = 1;

            @Override
            public DailyStatsIdResponse read() {
                if (currentIterator == null || !currentIterator.hasNext()) {
                    PageDto<DailyStatsIdResponse> page = dailyStatsService.findDailyStatsList(PageRequest.of(currentPage, 100), date);
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
    public ItemProcessor<DailyStatsIdResponse, MonthlyStats> monthlyItemProcessor(@Value("#{jobParameters['date']}") LocalDate date) {

        return item -> {

            long views = 0;
            long playtime = 0;

            LocalDate start = date.with(TemporalAdjusters.firstDayOfMonth());
            List<DailyStats> monthlyDataList = dailyStatsRepository.findBetweenDates(item.getBoardId(), start, date);

            for (DailyStats dailyStats : monthlyDataList) {
                views += dailyStats.getViews();
                playtime += dailyStats.getPlaytime();
            }

            return MonthlyStats.builder()
                    .accountId(item.getAccountId())
                    .boardId(item.getBoardId())
                    .views(views)
                    .playtime(playtime)
                    .date(date)
                    .build();
        };
    }

    @Bean
    @StepScope
    public ItemWriter<MonthlyStats> montlyStatsItemWriter() {
        return monthlyStatsRepository::saveAll;
    }
}
