package local.leporidaeyellow.infrastructure.qwolic_sql_exporter.service;

import local.leporidaeyellow.infrastructure.qwolic_sql_exporter.model.config.ConnectionEntity;
import local.leporidaeyellow.infrastructure.qwolic_sql_exporter.model.config.MetricEntity;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import local.leporidaeyellow.infrastructure.qwolic_sql_exporter.model.config.ConfigEntity;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import static local.leporidaeyellow.infrastructure.qwolic_sql_exporter.configuration.Constants.APPLICATION_CONFIGURATION_FILE;

@Service
public class ConfigService {
    private ConfigEntity config;

    public ConfigService() throws FileNotFoundException {
        InputStream inputStream = new FileInputStream(new File(APPLICATION_CONFIGURATION_FILE));
        Constructor constructor = new Constructor(ConfigEntity.class, new LoaderOptions());
        Yaml yaml = new Yaml(constructor);
        this.config = yaml.load(inputStream);
    }

    public List<ConnectionEntity> getConnectionList() {
        return this.config.getConnections();
    }

    public List<MetricEntity> getMetricsEntitiesList() {
        return this.config.getMetrics();
    }

    public List<MetricEntity> getMetricEntityList(String metricType) {
        return getMetricsEntitiesList()
                .stream()
                .filter(o -> o.getMetricType().contains(metricType))
                .toList();
    }
}
