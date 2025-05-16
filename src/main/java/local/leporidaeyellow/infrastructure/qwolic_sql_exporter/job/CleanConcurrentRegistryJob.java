package local.leporidaeyellow.infrastructure.qwolic_sql_exporter.job;

import local.leporidaeyellow.infrastructure.qwolic_sql_exporter.configuration.Constants;
import local.leporidaeyellow.infrastructure.qwolic_sql_exporter.service.CleanConcurrentRegistryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@EnableScheduling
public class CleanConcurrentRegistryJob {
    @Autowired
    CleanConcurrentRegistryService cleanConcurrentRegistryService;

    @Scheduled(cron = Constants.APP_PROPERTIES_SCHEDULER_PERIOD_CLEANER)
    public void taskForCleanBlockedQuery() {
        log.debug(Constants.DEBUG_LOG_START_CLEAN_BLOCKED_QUERIES);
        cleanConcurrentRegistryService.cleanRegistryByExpiredTimeout();
        log.debug(Constants.DEBUG_LOG_STOP_CLEAN_BLOCKED_QUERIES);
    }
}
