package local.leporidaeyellow.infrastructure.qwolic_sql_exporter.model.config;

import java.util.List;

public class ConfigEntity {
    private List<ConnectionEntity> connections;
    private List<MetricEntity> metrics;

    public List<ConnectionEntity> getConnections() {
        return connections;
    }

    public void setConnections(List<ConnectionEntity> connectionEntities) {
        this.connections = connectionEntities;
    }

    public List<MetricEntity> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<MetricEntity> metrics) {
        this.metrics = metrics;
    }

    @Override
    public String toString() {
        return "Config{" +
                "connections=" + connections +
                ", metrics=" + metrics +
                '}';
    }
}
