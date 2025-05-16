package local.leporidaeyellow.infrastructure.qwolic_sql_exporter.service;

import local.leporidaeyellow.infrastructure.qwolic_sql_exporter.model.data.MetricEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;

@Service
public class CleanConcurrentRegistryService {
    @Autowired
    ConcurrentMetricRegistryService concurrentRegistry;

    @Autowired
    ConfigService configService;

    public void cleanRegistryByExpiredTimeout() {
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
