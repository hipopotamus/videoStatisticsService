package statisticsservice.domain.monthlyStats.batch.cloud.step;

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
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.batch.item.support.builder.SynchronizedItemStreamReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import statisticsservice.domain.dailyStats.dto.DailyStatsIdResponse;
import statisticsservice.domain.dailyStats.entity.DailyStats;
import statisticsservice.domain.dailyStats.repository.DailyStatsRepository;
import statisticsservice.domain.dailyStats.service.DailyStatsService;
import statisticsservice.domain.monthlyStats.entity.MonthlyStats;
import statisticsservice.domain.monthlyStats.repository.MonthlyStatsJdbcRepository;
import statisticsservice.domain.monthlyStats.repository.MonthlyStatsRepository;
import statisticsservice.global.dto.PageDto;

import javax.sql.DataSource;
import java.sql.Date;
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
    private final MonthlyStatsJdbcRepository monthlyStatsJdbcRepository;
    private final int chunkSize = 100;

    @Bean
    public Step monthlyStatsStep(DataSource dataSource) {
        return new StepBuilder("monthlyStatsBatchStep", jobRepository)
                .<DailyStatsIdResponse, MonthlyStats>chunk(chunkSize, platformTransactionManager)
                .reader(monthlyItemReaderByCursorWithSynchro(null, dataSource))
                .processor(monthlyItemProcessor(null))
                .writer(monthlyStatsItemWriterByJdbc())
                .taskExecutor(monthlyStatsTaskExecutor())
                .faultTolerant()
                .retry(Exception.class)
                .retryLimit(3)
                .build();
    }

    @Bean
    @StepScope
    public ItemReader<DailyStatsIdResponse> monthlyItemReader(@Value("#{jobParameters['date']}") LocalDate date) {
        return new ItemReader<>() {

            private Iterator<DailyStatsIdResponse> currentIterator;
            private int currentPage = 0;

            @Override
            public DailyStatsIdResponse read() {
                if (currentIterator == null || !currentIterator.hasNext()) {
                    PageDto<DailyStatsIdResponse> page = dailyStatsService.findDailyStatsList(PageRequest.of(currentPage, chunkSize), date);
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
    public JdbcCursorItemReader<DailyStatsIdResponse> monthlyItemReaderByCursor(@Value("#{jobParameters['date']}") LocalDate date, DataSource dataSource) {
        return new JdbcCursorItemReaderBuilder<DailyStatsIdResponse>()
                .fetchSize(chunkSize)
                .dataSource(dataSource)
                .rowMapper(new BeanPropertyRowMapper<>(DailyStatsIdResponse.class))
                .name("monthlyCursorItemReader")
                .sql("select account_id, board_id from daily_stats where date = ?")
                .queryArguments(java.sql.Date.valueOf(date))
                .build();
    }

    @Bean
    @StepScope
    public SynchronizedItemStreamReader<DailyStatsIdResponse> monthlyItemReaderByCursorWithSynchro(@Value("#{jobParameters['date']}") LocalDate date, DataSource dataSource) {

        JdbcCursorItemReader<DailyStatsIdResponse> monthlyCursorItemReader = new JdbcCursorItemReaderBuilder<DailyStatsIdResponse>()
                .fetchSize(chunkSize)
                .dataSource(dataSource)
                .rowMapper(new BeanPropertyRowMapper<>(DailyStatsIdResponse.class))
                .name("monthlyCursorItemReader")
                .sql("select account_id, board_id from daily_stats where date = ?")
                .queryArguments(Date.valueOf(date))
                .build();

        return new SynchronizedItemStreamReaderBuilder<DailyStatsIdResponse>()
                .delegate(monthlyCursorItemReader)
                .build();
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
    public ItemWriter<MonthlyStats> monthlyStatsItemWriter() {
        return monthlyStatsRepository::saveAll;
    }

    @Bean
    @StepScope
    public ItemWriter<MonthlyStats> monthlyStatsItemWriterByJdbc() {
        return chunk -> monthlyStatsJdbcRepository.batchInsertMonthlyStats(chunk.getItems());
    }

    @Bean
    public TaskExecutor monthlyStatsTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(4);
        taskExecutor.setMaxPoolSize(8);
        taskExecutor.setQueueCapacity(100);
        taskExecutor.afterPropertiesSet();
        return taskExecutor;
    }
}
