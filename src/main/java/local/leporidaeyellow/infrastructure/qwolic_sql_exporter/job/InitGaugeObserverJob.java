package local.leporidaeyellow.infrastructure.qwolic_sql_exporter.job;

import local.leporidaeyellow.infrastructure.qwolic_sql_exporter.configuration.Constants;
import local.leporidaeyellow.infrastructure.qwolic_sql_exporter.service.ObserverService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@EnableScheduling
public class InitGaugeObserverJob {

    @Autowired
    ObserverService observerService;

    @Scheduled(cron = Constants.APP_PROPERTIES_SCHEDULER_PERIOD_OBSERVER)
    public void taskForGauges() {
        log.debug(Constants.DEBUG_LOG_START_GAUGE_OBSERVER);
        observerService.executeSqlForGauges();
        log.debug(Constants.DEBUG_LOG_STOP_GAUGE_OBSERVER);
    }
}
