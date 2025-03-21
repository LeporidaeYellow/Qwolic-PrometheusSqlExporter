package local.leporidaeyellow.infrastructure.qwolic_sql_exporter.service;

import io.micrometer.core.instrument.*;
import local.leporidaeyellow.infrastructure.qwolic_sql_exporter.model.config.MetricEntity;
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
    Map<String, Connection> connectionMap = new HashMap<>();

    MeterRegistry meterRegistry;

    @Autowired
    ConnectionService connectionService;

    public SqlExporterMetricsRegistry(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public Connection getConnection(MetricEntity metric) {
        return connectionMap.get(metric.getConcurrentRegistryName());
    }

    public Counter getCounter(MetricEntity metric) {
        if (!metricMap.keySet().equals(metric.getConcurrentRegistryName())) {
            Counter counter = Counter.builder(metric.getName())
                    .tags(metric.getLabels().toArray(new String[0]))
                    .description(metric.getDescription())
                    .register(meterRegistry);
            metricMap.put(metric.getConcurrentRegistryName(), counter);
        }
        return (Counter) metricMap.get(metric.getConcurrentRegistryName());
    }

    public Gauge getGauge(MetricEntity metric) {
        if (atomicIntegerMap.get(metric.getConcurrentRegistryName()) == null) {
            AtomicInteger atomicInteger = new AtomicInteger(-1);
            atomicIntegerMap.put(metric.getConcurrentRegistryName(), atomicInteger);

            Gauge.Builder<Supplier<Number>> build = Gauge.builder(metric.getName(), () -> atomicInteger);
            build.tags(metric.getLabels().toArray(new String[0]));
            build.description(metric.getDescription());
            Gauge gauge = build.register(meterRegistry);
            metricMap.put(metric.getConcurrentRegistryName(), gauge);
        }
        return (Gauge) metricMap.get(metric.getConcurrentRegistryName());
    }

    public Double executeQueryForDoubleValue(MetricEntity metric) {
        double metricValue;
        Connection connection = null;
        try {
            connection = connectionService.popConnection(metric.getConnectId());
            connectionMap.put(metric.getConcurrentRegistryName(), connection);
            connection.setReadOnly(true);
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(metric.getQuery());
            rs.next();
            metricValue = Double.parseDouble(rs.getString(1));
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            closeConnection(connection);
        }
        return metricValue;
    }

    public void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                if (!connection.isClosed()) {
                    System.out.println("Connection is not closed");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void setValueToMetrics(MetricEntity metric, CompletableFuture<Double> future) {
        if (metric.getMetricType().equals(METRIC_COUNTER)) {
            getCounter(metric).increment(future.join());
        }
        if (metric.getMetricType().equals(METRIC_GAUGE)) {
            getGauge(metric);
            AtomicInteger result = atomicIntegerMap.get(metric.getConcurrentRegistryName());
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
