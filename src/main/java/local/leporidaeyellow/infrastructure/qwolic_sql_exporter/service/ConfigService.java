package local.leporidaeyellow.infrastructure.qwolic_sql_exporter.service;

import local.leporidaeyellow.infrastructure.qwolic_sql_exporter.configuration.Constants;
import local.leporidaeyellow.infrastructure.qwolic_sql_exporter.model.data.ConnectionEntity;
import local.leporidaeyellow.infrastructure.qwolic_sql_exporter.model.data.MetricEntity;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
public class ConfigService {
    private ConfigEntity config;

    public ConfigService() {
        try {
            InputStream inputStream = new FileInputStream(new File(APPLICATION_CONFIGURATION_FILE));
            Constructor constructor = new Constructor(ConfigEntity.class, new LoaderOptions());
            Yaml yaml = new Yaml(constructor);
            this.config = yaml.load(inputStream);
        } catch (FileNotFoundException ex) {
            log.error(Constants.ERROR_LOG_WHILE_READING_CONFIG_FILE, ex);
        }
    }

    public List<ConnectionEntity> getConnectionList() {
        return this.config.getConnections();
    }

    public List<MetricEntity> getMetricsEntitiesList() {
        return this.config.getMetrics();
    }

    public List<MetricEntity> getMetricEntityListByType(String metricType) {
        return getMetricsEntitiesList()
                .stream()
                .filter(o -> o.getMetricType().equals(metricType))
                .toList();
    }
}
