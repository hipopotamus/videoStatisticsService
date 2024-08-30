package statisticsservice.global.config;

import com.p6spy.engine.logging.P6LogOptions;
import com.p6spy.engine.spy.P6SpyOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import statisticsservice.global.p6spy.P6spySqlFormatConfiguration;

@Configuration
public class AppConfig {

//    @PostConstruct
//    public void setLogMessageFormat() {
//        P6LogOptions.getActiveInstance().setFilter(true);
//        P6LogOptions.getActiveInstance().setExclude("BATCH");
//        P6SpyOptions.getActiveInstance().setLogMessageFormat(P6spySqlFormatConfiguration.class.getName());
//    }
}
