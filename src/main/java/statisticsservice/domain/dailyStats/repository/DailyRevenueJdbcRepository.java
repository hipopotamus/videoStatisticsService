package statisticsservice.domain.dailyStats.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import statisticsservice.domain.dailyStats.entity.DailyRevenue;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class DailyRevenueJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public void batchInsertDailyStats(List<? extends DailyRevenue> dailyRevenueList) {
        String sql = "INSERT INTO daily_revenue (account_id, date, revenue) VALUES (?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                DailyRevenue dailyRevenue = dailyRevenueList.get(i);
                ps.setLong(1, dailyRevenue.getAccountId());
                ps.setDate(2, Date.valueOf(dailyRevenue.getDate()));
                ps.setDouble(3, dailyRevenue.getRevenue());
            }

            @Override
            public int getBatchSize() {
                return dailyRevenueList.size();
            }
        });
    }
}
