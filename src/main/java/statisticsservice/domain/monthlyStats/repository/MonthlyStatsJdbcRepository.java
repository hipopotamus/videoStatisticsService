package statisticsservice.domain.monthlyStats.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import statisticsservice.domain.monthlyStats.entity.MonthlyStats;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MonthlyStatsJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public void batchInsertMonthlyStats(List<? extends MonthlyStats> monthlyStatsList) {

        String sql = "INSERT INTO monthly_stats (account_id, board_id, views, playtime, date) VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                MonthlyStats monthlyStats = monthlyStatsList.get(i);
                ps.setLong(1, monthlyStats.getAccountId());
                ps.setLong(2, monthlyStats.getBoardId());
                ps.setLong(3, monthlyStats.getViews());
                ps.setLong(4, monthlyStats.getPlaytime());
                ps.setDate(5, Date.valueOf(monthlyStats.getDate()));
            }

            @Override
            public int getBatchSize() {
                return monthlyStatsList.size();
            }
        });
    }
}
