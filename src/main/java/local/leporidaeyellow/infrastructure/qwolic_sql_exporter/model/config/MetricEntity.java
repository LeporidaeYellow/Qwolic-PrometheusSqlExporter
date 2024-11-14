package local.leporidaeyellow.infrastructure.qwolic_sql_exporter.model.config;

import java.util.List;

public class MetricEntity {
    private String name;
    private String description;
    private String query;
    private List<String> labels;
    private String metricType;
    private String connectId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public String getMetricType() {
        return metricType;
    }

    public void setMetricType(String metricType) {
        this.metricType = metricType;
    }

    public String getConnectId() {
        return connectId;
    }

    public void setConnectId(String connectId) {
        this.connectId = connectId;
    }

    public  String getConcurrentRegistryName() {
        return name + "_" + metricType;
    }

    @Override
    public String toString() {
        return "MetricEntity{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", query='" + query + '\'' +
                ", labels=" + labels +
                ", metricType='" + metricType + '\'' +
                ", connectId='" + connectId + '\'' +
                '}';
    }
}