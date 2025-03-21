package local.leporidaeyellow.infrastructure.qwolic_sql_exporter.job;

import local.leporidaeyellow.infrastructure.qwolic_sql_exporter.service.ConcurrentMetricRegistryService;
import local.leporidaeyellow.infrastructure.qwolic_sql_exporter.service.ConfigService;
import local.leporidaeyellow.infrastructure.qwolic_sql_exporter.configuration.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
@EnableScheduling
public class InitGaugeObserverJob {

    @Autowired
    ConfigService configService;

    @Autowired
    ConcurrentMetricRegistryService concurrentRegistry;

    @Scheduled(cron = "${scheduler.period}")
    public void taskForGauges() {
        executeSqlForGauges();
    }

    void executeSqlForGauges() {
        configService
                .getMetricEntityListByType(Constants.METRIC_GAUGE)
                .parallelStream()
                .forEach(metric -> {
                    try {
                        concurrentRegistry.proceedGettingMetric(metric);
                    } catch (ExecutionException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}
