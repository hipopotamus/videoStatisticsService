package statisticsservice.domain.weeklyStats.batch.cloud.step;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.transaction.PlatformTransactionManager;
import statisticsservice.domain.dailyStats.dto.DailyStatsIdResponse;
import statisticsservice.domain.dailyStats.entity.DailyStats;
import statisticsservice.domain.dailyStats.repository.DailyStatsRepository;
import statisticsservice.domain.dailyStats.service.DailyStatsService;
import statisticsservice.domain.weeklyStats.entity.WeeklyStats;
import statisticsservice.domain.weeklyStats.repository.WeeklyStatsRepository;
import statisticsservice.global.dto.PageDto;

import javax.sql.DataSource;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Iterator;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WeeklyStatsBatchStep {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final DailyStatsRepository dailyStatsRepository;
    private final WeeklyStatsRepository weeklyStatsRepository;
    private final DailyStatsService dailyStatsService;

    @Bean
    public Step weeklyStatsStep(DataSource dataSource) {
        return new StepBuilder("weeklyStatsBatchStep", jobRepository)
                .<DailyStatsIdResponse, WeeklyStats>chunk(100, platformTransactionManager)
                .reader(weeklyStatsItemReader(null))
                .processor(weeklyStatsItemProcessor(null))
                .writer(weeklyStatsItemWriter())
                .build();
    }

    @Bean
    @StepScope
    public ItemReader<DailyStatsIdResponse> weeklyStatsItemReader(@Value("#{jobParameters['date']}") LocalDate date) {
        return new ItemReader<>() {

            private Iterator<DailyStatsIdResponse> currentIterator;
            private int currentPage = 0;

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
    public JdbcCursorItemReader<DailyStatsIdResponse> weeklyItemReaderByCursor(@Value("#{jobParameters['date']}") LocalDate date, DataSource dataSource) {
        return new JdbcCursorItemReaderBuilder<DailyStatsIdResponse>()
                .fetchSize(100)
                .dataSource(dataSource)
                .rowMapper(new BeanPropertyRowMapper<>(DailyStatsIdResponse.class))
                .name("weeklyCursorItemReader")
                .sql("select account_id, board_id from daily_stats where date = ?")
                .queryArguments(java.sql.Date.valueOf(date))
                .build();
    }

    @Bean
    @StepScope
    public ItemProcessor<DailyStatsIdResponse, WeeklyStats> weeklyStatsItemProcessor(@Value("#{jobParameters['date']}") LocalDate date) {

        return item -> {

            long views = 0;
            long playtime = 0;

            LocalDate start = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            List<DailyStats> weeklyDataList = dailyStatsRepository.findBetweenDates(item.getBoardId(), start, date);

            for (DailyStats dailyStats : weeklyDataList) {
                views += dailyStats.getViews();
                playtime += dailyStats.getPlaytime();
            }

            return WeeklyStats.builder()
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
    public ItemWriter<WeeklyStats> weeklyStatsItemWriter() {
        return weeklyStatsRepository::saveAll;
    }
}
