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
import statisticsservice.domain.dailyStats.entity.DailyRevenue;
import statisticsservice.domain.dailyStats.entity.DailyStats;
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

    @Bean
    public Step dailyRevenueStep() {
        return new StepBuilder("dailyRevenueBatchStep", jobRepository)
                .<AccountIdResponse, DailyRevenue>chunk(100, platformTransactionManager)
                .reader(accountIdItemReader(null))
                .processor(accountIdItemProcessor(null))
                .writer(dailyRevenueItemWriter())
                .build();
    }

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
                    .date(currentDate)
                    .revenue(totalRevenue)
                    .build();
        };
    }

    @Bean
    @StepScope
    public ItemWriter<DailyRevenue> dailyRevenueItemWriter() {
        return dailyRevenueRepository::saveAll;
    }
}
