package local.leporidaeyellow.infrastructure.qwolic_sql_exporter.service;

import local.leporidaeyellow.infrastructure.qwolic_sql_exporter.model.config.MetricEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

@Service
public class ConcurrentMetricRegistryService {
    private final Map<String, CompletableFuture<?>> futureMap = new ConcurrentHashMap<>();
    private final Map<String, Instant> timestampMap = new HashMap<>();

    @Autowired
    SqlExporterMetricsRegistry sqlExporterMetricsRegistry;

    public CompletableFuture<?> getFuture(String name) {
        return futureMap.get(name);
    }

    public void setFuture(String name,CompletableFuture<?> future) {
        futureMap.put(name, future);
    }

    public Boolean isDoneFuture(String name) {
        return getFuture(name).isDone();
    }

    public Boolean isExistFuture(String name) {
        return futureMap.containsKey(name);
    }

    public void setTimestamp(String name, Instant timestamp) {
        timestampMap.put(name, timestamp);
    }

    public Set<String> getKeysFromFutureMap() {
        return futureMap.keySet();
    }

    public Instant getTimestamp(String name) {
        return timestampMap.get(name);
    }

    public CompletableFuture<Double> getResult(MetricEntity metric) {
        return CompletableFuture.supplyAsync(() -> sqlExporterMetricsRegistry.executeQueryForDoubleValue(metric));
    }

    public void proceedGettingMetric(MetricEntity metric) throws ExecutionException, InterruptedException {
        CompletableFuture<Double> future = getResult(metric);
        if (executePermission(metric)) {
            setFuture(metric.getConcurrentRegistryName(), future);
            setTimestamp(metric.getConcurrentRegistryName(), Instant.now());
            sqlExporterMetricsRegistry.setValueToMetrics(metric, future);
        }
    }

    public Boolean executePermission(MetricEntity metric) {
        return getFuture(metric.getConcurrentRegistryName()) == null ||
                isDoneFuture(metric.getConcurrentRegistryName()) ||
                !isExistFuture(metric.getConcurrentRegistryName());
    }

    public long getDurationFromTimestampMap(MetricEntity metric) {
        return getTimestamp(metric.getConcurrentRegistryName()).until(Instant.now(), ChronoUnit.SECONDS);
    }
}
