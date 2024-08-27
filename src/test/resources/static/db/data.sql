INSERT INTO daily_revenue(daily_revenue_id, account_id, date, revenue)
VALUES (10001, 10001, '2024-01-01', 2040);

INSERT INTO daily_stats(daily_stats_id, account_id, board_id, views, total_views, ad_views, total_ad_views, playtime, total_playtime, video_revenue, ad_video_revenue, revenue, date)
VALUES (20001, 10001, 40001, 10, 10, 5, 5, 300, 300, 10, 500, 510, '2024-01-01');
INSERT INTO daily_stats(daily_stats_id, account_id, board_id, views, total_views, ad_views, total_ad_views, playtime, total_playtime, video_revenue, ad_video_revenue, revenue, date)
VALUES (20002, 10001, 40002, 10, 10, 5, 5, 300, 300, 10, 500, 510, '2024-01-01');
INSERT INTO daily_stats(daily_stats_id, account_id, board_id, views, total_views, ad_views, total_ad_views, playtime, total_playtime, video_revenue, ad_video_revenue, revenue, date)
VALUES (20003, 10001, 40003, 10, 10, 5, 5, 300, 300, 10, 500, 510, '2024-01-01');
INSERT INTO daily_stats(daily_stats_id, account_id, board_id, views, total_views, ad_views, total_ad_views, playtime, total_playtime, video_revenue, ad_video_revenue, revenue, date)
VALUES (20004, 10001, 40004, 10, 10, 5, 5, 300, 300, 10, 500, 510, '2024-01-01');

INSERT INTO daily_top_board(daily_top_board_id, date, board_id_list_by_views, board_id_list_by_playtime)
VALUES (30001, '2024-01-01', '[40001, 40002, 40003, 40004, 40005]', '[40001, 40002, 40003, 40004, 40005]');

INSERT INTO monthly_top_board(monthly_top_board_id, date, board_id_list_by_views, board_id_list_by_playtime)
VALUES (40001, '2024-01-01', '[40001, 40002, 40003, 40004, 40005]', '[40001, 40002, 40003, 40004, 40005]');

INSERT INTO weekly_top_board(weekly_top_board_id, date, board_id_list_by_views, board_id_list_by_playtime)
VALUES (50001, '2024-01-01', '[40001, 40002, 40003, 40004, 40005]', '[40001, 40002, 40003, 40004, 40005]');