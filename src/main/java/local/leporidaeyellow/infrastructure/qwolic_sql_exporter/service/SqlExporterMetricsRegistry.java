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
        double metricValue = -1.0;
        Connection connection = null;
        try {
            connection = connectionService.popConnection(metric.getConnectId());
            connectionMap.put(metric.getConcurrentRegistryName(), connection);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(Long.valueOf(metric.getTimeout()).intValue());
            ResultSet rs = statement.executeQuery(metric.getQuery());
            if (rs.next()) {
                metricValue = Double.parseDouble(rs.getString(1));
            }
        } catch (SQLException | ClassNotFoundException ex) {
            logError("Ошибка при выполнении запроса: ", ex); // Логируем ошибку
        } finally {
            closeResources(rs, statement, connection); // Закрываем ресурсы
        }
        return metricValue;
    }

        /** * Безопасно закрывает объекты JDBC, игнорируя возможные исключения. */
    private void closeResources(ResultSet rs, Statement stmt, Connection conn) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException ignored) {}
        }
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException ignored) {}
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ignored) {}
        }
    }

    // public void closeConnection(Connection connection) {
    //     if (connection != null) {
    //         try {
    //             connection.close();
    //             if (!connection.isClosed()) {
    //                 System.out.println("Connection is not closed");
    //             }
    //         } catch (SQLException e) {
    //             throw new RuntimeException(e);
    //         }
    //     }
    // }

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

    /** * Простейшая реализация логирования ошибок. */
    private void logError(String message, Exception exception) {
        System.err.println(message + exception.getMessage());
        exception.printStackTrace(System.err);
    }

    @Override
    public String toString() {
        return "SqlExporterMetricsRegistry{" +
                "objectMap=" + metricMap +
                '}';
    }
}
