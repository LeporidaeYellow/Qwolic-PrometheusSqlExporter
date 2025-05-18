package local.leporidaeyellow.infrastructure.qwolic_sql_exporter.service;

import local.leporidaeyellow.infrastructure.qwolic_sql_exporter.configuration.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ObserverService {
    @Autowired
    ConfigService configService;

    @Autowired
    ConcurrentMetricRegistryService concurrentRegistry;

    public void executeSqlForCounters() {
        configService
                .getMetricEntityListByType(Constants.METRIC_COUNTER)
//                .parallelStream()
                .forEach(metric -> {
                    concurrentRegistry.proceedGettingMetric(metric);
                });
    }

    public void executeSqlForGauges() {
        configService
                .getMetricEntityListByType(Constants.METRIC_GAUGE)
//                .parallelStream()
                .forEach(metric -> {
                    concurrentRegistry.proceedGettingMetric(metric);
                });
    }
}
