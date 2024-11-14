package local.leporidaeyellow.infrastructure.qwolic_sql_exporter.service;

import local.leporidaeyellow.infrastructure.qwolic_sql_exporter.model.config.MetricEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

@Service
public class ConcurrentMetricRegistryService {

    @Autowired
    SqlExporterMetricsRegistry sqlExporterMetricsRegistry;

    private final Map<String, CompletableFuture<?>> map = new ConcurrentHashMap<>();

    public CompletableFuture<?> getFuture(String name) {
        return map.get(name);
    }

    public void setFuture(String name,CompletableFuture<?> future) {
        map.put(name, future);
    }

    public Boolean isDoneFuture(String name) {
        return getFuture(name).isDone();
    }

    public Boolean isExistFuture(String name) {
        return map.containsKey(name);
    }

    public CompletableFuture<Double> getResult(MetricEntity metric) {
        return CompletableFuture.supplyAsync(() -> sqlExporterMetricsRegistry.executeQueryForDoubleValue(metric));
    }

    public void proceedGettingMetric(MetricEntity metric) throws ExecutionException, InterruptedException {
        CompletableFuture<Double> future = getResult(metric);
        if (executePermission(metric)) {
            setFuture(metric.getConcurrentRegistryName(), future);
            sqlExporterMetricsRegistry.setValueToMetrics(metric, future);
        }
    }

    public Boolean executePermission(MetricEntity metric) {
        return getFuture(metric.getConcurrentRegistryName()) == null ||
                isDoneFuture(metric.getConcurrentRegistryName()) ||
                !isExistFuture(metric.getConcurrentRegistryName());
    }
}
