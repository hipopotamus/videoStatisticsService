package statisticsservice.global.config;

import com.p6spy.engine.logging.P6LogOptions;
import com.p6spy.engine.spy.P6SpyOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import statisticsservice.global.p6spy.P6spySqlFormatConfiguration;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class AppConfig {

//    @PostConstruct
//    public void setLogMessageFormat() {
//        P6LogOptions.getActiveInstance().setFilter(true);
//        P6LogOptions.getActiveInstance().setExclude("BATCH");
//        P6SpyOptions.getActiveInstance().setLogMessageFormat(P6spySqlFormatConfiguration.class.getName());
//    }
}
