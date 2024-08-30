package statisticsservice.domain.dailyStats.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import statisticsservice.domain.dailyStats.entity.DailyStats;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class DailyStatsJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public void batchInsertDailyStats(List<? extends DailyStats> dailyStatsList) {
        String sql = "INSERT INTO daily_stats (" +
                "account_id, board_id, views, total_views, ad_views, total_ad_views, " +
                "playtime, total_playtime, video_revenue, ad_video_revenue, revenue, date) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                DailyStats dailyStats = dailyStatsList.get(i);
                ps.setLong(1, dailyStats.getAccountId());
                ps.setLong(2, dailyStats.getBoardId());
                ps.setLong(3, dailyStats.getViews());
                ps.setLong(4, dailyStats.getTotalViews());
                ps.setLong(5, dailyStats.getAdViews());
                ps.setLong(6, dailyStats.getTotalAdViews());
                ps.setLong(7, dailyStats.getPlaytime());
                ps.setLong(8, dailyStats.getTotalPlaytime());
                ps.setDouble(9, dailyStats.getVideoRevenue());
                ps.setDouble(10, dailyStats.getAdVideoRevenue());
                ps.setDouble(11, dailyStats.getRevenue());
                ps.setDate(12, Date.valueOf(dailyStats.getDate()));
            }

            @Override
            public int getBatchSize() {
                return dailyStatsList.size();
            }
        });
    }

}
