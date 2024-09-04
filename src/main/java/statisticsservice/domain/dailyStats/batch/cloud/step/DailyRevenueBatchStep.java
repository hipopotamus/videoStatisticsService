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
import statisticsservice.domain.dailyStats.entity.DailyRevenue;
import statisticsservice.domain.dailyStats.entity.DailyStats;
import statisticsservice.domain.dailyStats.repository.DailyRevenueJdbcRepository;
import statisticsservice.domain.dailyStats.repository.DailyRevenueRepository;
import statisticsservice.domain.dailyStats.repository.DailyStatsRepository;
import statisticsservice.external.videoservice.client.VideoServiceClient;
import statisticsservice.external.videoservice.dto.AccountIdResponse;
import statisticsservice.global.dto.PageDto;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DailyRevenueBatchStep {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final VideoServiceClient videoServiceClient;
    private final DailyStatsRepository dailyStatsRepository;
    private final DailyRevenueRepository dailyRevenueRepository;
    private final DailyRevenueJdbcRepository dailyRevenueJdbcRepository;
    private final int chunkSize = 100;

    @Bean
    public Step dailyRevenueStep() {
        return new StepBuilder("dailyRevenueBatchStep", jobRepository)
                .<AccountIdResponse, DailyRevenue>chunk(chunkSize, platformTransactionManager)
                .reader(accountIdItemReaderByCursorWithSynchro(null))
                .processor(accountIdItemProcessor(null))
                .writer(dailyRevenueItemWriterByJdbc())
                .taskExecutor(dailyRevenueTaskExecutor())
                .build();
    }

    @Bean
    @StepScope
    public ItemReader<AccountIdResponse> accountIdItemReader(@Value("#{jobParameters['date']}") LocalDate date) {
        return new ItemReader<>() {

            private Iterator<AccountIdResponse> currentIterator;
            private int currentPage = 1;

            @Override
            public AccountIdResponse read() {
                if (currentIterator == null || !currentIterator.hasNext()) {
                    PageDto<AccountIdResponse> page = videoServiceClient.accountList(PageRequest.of(currentPage, chunkSize));
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
    public ItemReader<AccountIdResponse> accountIdItemReaderByCursor(@Value("#{jobParameters['date']}") LocalDate date) {
        return new ItemReader<>() {

            private Iterator<AccountIdResponse> currentIterator;
            private long lastAccountId = 0;

            @Override
            public AccountIdResponse read() {
                if (currentIterator == null || !currentIterator.hasNext()) {
                    List<AccountIdResponse> responseList = videoServiceClient.accountIdCursor(lastAccountId, chunkSize);
                    if (responseList == null || responseList.isEmpty()) {
                        return null;
                    }
                    currentIterator = responseList.iterator();
                    lastAccountId = responseList.getLast().getAccountId();
                }

                return currentIterator.hasNext() ? currentIterator.next() : null;
            }
        };
    }

    @Bean
    @StepScope
    public SynchronizedItemReader<AccountIdResponse> accountIdItemReaderByCursorWithSynchro(@Value("#{jobParameters['date']}") LocalDate date) {
        ItemReader<AccountIdResponse> itemReader = new ItemReader<>() {

            private Iterator<AccountIdResponse> currentIterator;
            private long lastAccountId = 0;

            @Override
            public AccountIdResponse read() {
                if (currentIterator == null || !currentIterator.hasNext()) {
                    List<AccountIdResponse> responseList = videoServiceClient.accountIdCursor(lastAccountId, chunkSize);
                    if (responseList == null || responseList.isEmpty()) {
                        return null;
                    }
                    currentIterator = responseList.iterator();
                    lastAccountId = responseList.getLast().getAccountId();
                }

                return currentIterator.hasNext() ? currentIterator.next() : null;
            }
        };

        return new SynchronizedItemReaderBuilder<AccountIdResponse>()
                .delegate(itemReader)
                .build();
    }

    @Bean
    @StepScope
    public ItemProcessor<AccountIdResponse, DailyRevenue> accountIdItemProcessor(@Value("#{jobParameters['date']}") LocalDate date) {
        return item -> {

            List<DailyStats> dailyStatsList = dailyStatsRepository.findByAccountIdAndDate(item.getAccountId(), date);

            double totalRevenue = 0;
            for (DailyStats dailyStats : dailyStatsList) {
                totalRevenue += dailyStats.getRevenue();
            }

            return DailyRevenue.builder()
                    .accountId(item.getAccountId())
                    .date(date)
                    .revenue(totalRevenue)
                    .build();
        };
    }

    @Bean
    @StepScope
    public ItemWriter<DailyRevenue> dailyRevenueItemWriter() {
        return dailyRevenueRepository::saveAll;
    }

    @Bean
    @StepScope
    public ItemWriter<DailyRevenue> dailyRevenueItemWriterByJdbc() {
        return chunk -> dailyRevenueJdbcRepository.batchInsertDailyStats(chunk.getItems());
    }

    @Bean
    public TaskExecutor dailyRevenueTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(4);
        taskExecutor.setMaxPoolSize(8);
        taskExecutor.setQueueCapacity(100);
        taskExecutor.afterPropertiesSet();
        return taskExecutor;
    }
}
