package local.leporidaeyellow.infrastructure.qwolic_sql_exporter.service;

import io.micrometer.core.instrument.*;
import local.leporidaeyellow.infrastructure.qwolic_sql_exporter.model.config.MetricEntity;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static local.leporidaeyellow.infrastructure.qwolic_sql_exporter.configuration.Constants.METRIC_COUNTER;
import static local.leporidaeyellow.infrastructure.qwolic_sql_exporter.configuration.Constants.METRIC_GAUGE;

@Service
public class SqlExporterMetricsRegistry {
    Map<String, Object> metricMap = new HashMap<>();
    Map<String, AtomicInteger> atomicIntegerMap = new HashMap<>();

    MeterRegistry meterRegistry;

    @Autowired
    ConnectionService connectionService;

    public SqlExporterMetricsRegistry(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public Counter getCounter(MetricEntity metricEntity) {
        if (!metricMap.keySet().contains(metricEntity.getName())) {
            Counter counter = Counter.builder(metricEntity.getName())
                    .tags(metricEntity.getLabels().toArray(new String[0]))
                    .description(metricEntity.getDescription())
                    .register(meterRegistry);
            metricMap.put(metricEntity.getName(), counter);
        }
        return (Counter) metricMap.get(metricEntity.getName());
    }

    public Gauge getGauge(MetricEntity metricEntity) {
        if (!metricMap.keySet().contains(metricEntity.getName())) {
            AtomicInteger atomicInteger = new AtomicInteger(-1);
            atomicIntegerMap.put(metricEntity.getName(), atomicInteger);

            Gauge.Builder<Supplier<Number>> build = Gauge.builder(metricEntity.getName(), () -> atomicInteger);
            build.tags(metricEntity.getLabels().toArray(new String[0]));
            build.description(metricEntity.getDescription());
            Gauge gauge = build.register(meterRegistry);
            metricMap.put(metricEntity.getName(), gauge);
        }
        return (Gauge) metricMap.get(metricEntity.getName());
    }

    public Double executeQueryForDoubleValue(MetricEntity metric) {
        double metricValue;
        Connection connection;

        try {
            connection = connectionService.popConnection(metric.getConnectId());
            connection.setReadOnly(true);
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(metric.getQuery());
            rs.next();
            metricValue = Double.parseDouble(rs.getString(1));
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        try {
            connection.close();
            if (!connection.isClosed()) {
                System.out.println("Connection is not closed");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return metricValue;
    }

    public void setValueToMetrics(MetricEntity metric, CompletableFuture<Double> future) {
        if (metric.getMetricType().contains(METRIC_COUNTER)) {
            getCounter(metric).increment(future.join());
        }
        if (metric.getMetricType().contains(METRIC_GAUGE)) {
            getGauge(metric);
            val result = atomicIntegerMap.get(metric.getName());
            result.set(future.join().intValue());
        }
    }

    @Override
    public String toString() {
        return "SqlExporterMetricsRegistry{" +
                "objectMap=" + metricMap +
                '}';
    }
}
