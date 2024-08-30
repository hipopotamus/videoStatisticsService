package statisticsservice.domain.weeklyStats.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import statisticsservice.domain.monthlyStats.entity.MonthlyStats;
import statisticsservice.domain.weeklyStats.entity.WeeklyStats;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class WeeklyStatsJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public void batchInsertWeeklyStats(List<? extends WeeklyStats> weeklyStatsList) {

        String sql = "INSERT INTO weekly_stats (account_id, board_id, views, playtime, date) VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                WeeklyStats weeklyStats = weeklyStatsList.get(i);
                ps.setLong(1, weeklyStats.getAccountId());
                ps.setLong(2, weeklyStats.getBoardId());
                ps.setLong(3, weeklyStats.getViews());
                ps.setLong(4, weeklyStats.getPlaytime());
                ps.setDate(5, Date.valueOf(weeklyStats.getDate()));
            }

            @Override
            public int getBatchSize() {
                return weeklyStatsList.size();
            }
        });
    }
}
