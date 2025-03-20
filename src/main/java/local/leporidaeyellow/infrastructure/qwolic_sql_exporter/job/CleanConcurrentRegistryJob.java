package local.leporidaeyellow.infrastructure.qwolic_sql_exporter.job;

import local.leporidaeyellow.infrastructure.qwolic_sql_exporter.model.config.MetricEntity;
import local.leporidaeyellow.infrastructure.qwolic_sql_exporter.service.ConcurrentMetricRegistryService;
import local.leporidaeyellow.infrastructure.qwolic_sql_exporter.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;

@Service
@EnableScheduling
public class CleanConcurrentRegistryJob {
    @Autowired
    ConcurrentMetricRegistryService concurrentRegistry;

    @Autowired
    ConfigService configService;

    @Scheduled(cron = "${scheduler.period}")
    public void taskForCleanBlockedQuery() {
        cleanRegistryByExpiredTimeout();
    }

    void cleanRegistryByExpiredTimeout() {
        concurrentRegistry
                .getKeysFromFutureMap()
                .forEach(futureName -> {
                    Future<?> future = concurrentRegistry.getFuture(futureName);
                    if (timeoutIsExpired(futureName) && !future.isDone()) {
                        future.cancel(true);
                    }
                });
    }

    Boolean timeoutIsExpired(String futureName) {
        for (MetricEntity metric :configService.getMetricsEntitiesList()) {
            if (metric.getConcurrentRegistryName().equals(futureName))
                return metric.getTimeout() < concurrentRegistry.getDurationFromTimestampMap(metric);
        }
        return false;
    }
}
