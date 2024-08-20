package statisticsservice.domain.statistics.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RevenueService {

    public double calculateVideoRevenue(long totalViews, long dailyViews) {
        return getVideoUnitPrice(totalViews) * dailyViews;
    }

    public double calculateAdVideoRevenue(long totalAdViews, long dailyAdViews) {
        return getAdVideoUnitPrice(totalAdViews) * dailyAdViews;
    }

    private double getVideoUnitPrice(long totalViews) {
        if (totalViews < 100000) {
            return 1;
        } else if (totalViews < 500000) {
            return 1.1;
        } else if (totalViews < 1000000) {
            return 1.2;
        } else {
            return 1.3;
        }
    }

    private double getAdVideoUnitPrice(long totalViews) {
        if (totalViews < 100000) {
            return 10;
        } else if (totalViews < 500000) {
            return 12;
        } else if (totalViews < 1000000) {
            return 15;
        } else {
            return 20;
        }
    }
}
