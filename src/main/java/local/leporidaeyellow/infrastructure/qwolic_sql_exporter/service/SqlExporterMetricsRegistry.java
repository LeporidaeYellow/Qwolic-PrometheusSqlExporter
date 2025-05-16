package local.leporidaeyellow.infrastructure.qwolic_sql_exporter.service;

import io.micrometer.core.instrument.*;
import local.leporidaeyellow.infrastructure.qwolic_sql_exporter.configuration.Constants;
import local.leporidaeyellow.infrastructure.qwolic_sql_exporter.model.data.MetricEntity;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
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
            AtomicInteger atomicInteger = new AtomicInteger(Constants.START_VALUE_METRIC_GAUGE);
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
        double metricValue = Constants.START_VALUE_METRIC_GAUGE;
        Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;
        try {
            connection = connectionService.popConnection(metric.getConnectId());
            if (connection == null) {
                log.info(String.format(Constants.INFO_LOG_SET_PREVIOUS_VALUES_METRICS_CAUSE_CONNECTION_WITH_FORMATTED_STRING, metric.getConnectId()));
                return Double.valueOf(String.valueOf(atomicIntegerMap.get(metric.getConcurrentRegistryName())));
            }
            connectionMap.put(metric.getConcurrentRegistryName(), connection);
            statement = connection.createStatement();
            statement.setQueryTimeout(Long.valueOf(metric.getTimeout()).intValue());
            rs = statement.executeQuery(metric.getQuery());
            if (rs.next()) {
                metricValue = Double.parseDouble(rs.getString(1));
            }
        } catch (SQLException | ClassNotFoundException ex) {
            log.error(Constants.ERROR_LOG_WHILE_REQUEST_EXECUTION, ex); // logging error
        } finally {
            connectionService.releaseConnection(metric.getConnectId(), connection); // return connection to pull
            try {
                statement.close();
            } catch (SQLException ex) {
                log.error(Constants.ERROR_LOG_WHILE_CAN_NOT_CLOSE_STATEMENT, ex);
            }
            try {
                rs.close();
            } catch (SQLException ex) {
                log.error(Constants.ERROR_LOG_WHILE_CAN_NOT_CLOSE_RESULTSET, ex);
            }
        }
        return metricValue;
    }

    public void setValueToMetrics(MetricEntity metric, CompletableFuture<Double> future) {
        if (future == null) {
            log.info(String.format(Constants.INFO_LOG_SET_PREVIOUS_VALUES_METRICS_CAUSE_EXECUTION_WITH_FORMATTED_STRING, metric.getQuery()));
            Double.valueOf(String.valueOf(atomicIntegerMap.get(metric.getConcurrentRegistryName())));
        } else {
            if (metric.getMetricType().equals(Constants.METRIC_COUNTER)) {
                getCounter(metric).increment(future.join());
            }
            if (metric.getMetricType().equals(Constants.METRIC_GAUGE)) {
                getGauge(metric);
                AtomicInteger result = atomicIntegerMap.get(metric.getConcurrentRegistryName());
                result.set(future.join().intValue());
            }
        }
    }

    @Override
    public String toString() {
        return "SqlExporterMetricsRegistry{" +
                "objectMap=" + metricMap +
                '}';
    }
}
