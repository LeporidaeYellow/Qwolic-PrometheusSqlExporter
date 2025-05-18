package local.leporidaeyellow.infrastructure.qwolic_sql_exporter.service;

import local.leporidaeyellow.infrastructure.qwolic_sql_exporter.configuration.Constants;
import local.leporidaeyellow.infrastructure.qwolic_sql_exporter.configuration.SqlExporterAsyncExecutorConfiguration;
import local.leporidaeyellow.infrastructure.qwolic_sql_exporter.model.data.MetricEntity;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class ConcurrentMetricRegistryService {
    private final Map<String, CompletableFuture<?>> futureMap = new ConcurrentHashMap<>();
    private final Map<String, Instant> timestampMap = new HashMap<>();

    @Autowired
    SqlExporterAsyncExecutorConfiguration asyncExecutor;

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

    @Async("sqlExporterAsyncExecutor")
    public CompletableFuture<Double> getResult(MetricEntity metric) {
        log.debug(String.format(Constants.DEBUG_LOG_SQL_QUERY_EXECUTE_WITH_FORMATTED_STRING, metric.getQuery()));
        val tsk = CompletableFuture.supplyAsync(() -> sqlExporterMetricsRegistry.executeQueryForDoubleValue(metric), asyncExecutor.sqlExporterAsyncExecutor());
        log.debug(String.format(Constants.DEBUG_LOG_SQL_QUERY_DONE_WITH_FORMATTED_STRING, metric.getQuery()));
        return tsk;
    }

    public void proceedGettingMetric(MetricEntity metric){
        try {
            CompletableFuture<Double> future = getResult(metric);
            if (executePermission(metric)) {
                setFuture(metric.getConcurrentRegistryName(), future);
                setTimestamp(metric.getConcurrentRegistryName(), Instant.now());
                sqlExporterMetricsRegistry.setValueToMetrics(metric, future);
            }
        } catch (Exception ex) {
            // send request with null-future, for set up previous values
            sqlExporterMetricsRegistry.setValueToMetrics(metric, null);
            log.error(Constants.ERROR_LOG_WHILE_PROCEEDING_GETTING_METRICS, ex);
            futureMap.get(metric.getConcurrentRegistryName()).cancel(true);
            futureMap.remove(metric.getConcurrentRegistryName());
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
